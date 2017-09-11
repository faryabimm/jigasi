/*
 * Jigasi, the JItsi GAteway to SIP.
 *
 * Copyright @ 2017 Atlassian Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jitsi.jigasi.transcription;

import net.java.sip.communicator.service.protocol.*;
import org.json.simple.*;

import java.util.*;

/**
 * Pushes transcriptions to remote services.
 *
 * @author Damian Minkov
 */
public class RemotePublisherTranscriptionHandler
    extends LocalJsonTranscriptHandler
{
    /**
     * List of remote services to notify for transcriptions.
     */
    private List<String> urls = new ArrayList<>();

    /**
     * Constructs RemotePublisherTranscriptionHandler, initializing its config.
     *
     * @param urlsStr String containing urls of remote services, separated
     * by ','.
     */
    public RemotePublisherTranscriptionHandler(String urlsStr)
    {
        super();

        // initialize tokens
        StringTokenizer tokens = new StringTokenizer(urlsStr, ",");
        while (tokens.hasMoreTokens())
        {
            urls.add(tokens.nextToken().trim());
        }
    }

    @Override
    public void publish(ChatRoom room, TranscriptionResult result)
    {
        if (result.isInterim())
            return;

        JSONObject eventObject = createJSONObject(result);

        JSONObject encapsulatingObject = new JSONObject();
        createEncapsulatingObject(encapsulatingObject, eventObject);

        encapsulatingObject.put(
            LocalJsonTranscriptHandler
                .JSON_KEY_FINAL_TRANSCRIPT_ROOM_NAME,
            result.getParticipant().getTranscriber().getRoomName());

        for (String url : urls)
        {
            Util.postJSON(url, encapsulatingObject);
        }
    }
}
