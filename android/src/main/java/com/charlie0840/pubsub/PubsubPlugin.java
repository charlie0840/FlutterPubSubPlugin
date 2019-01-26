package com.charlie0840.pubsub;


import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** PubsubPlugin */
public class PubsubPlugin implements MethodCallHandler {
    /** Plugin registration. */
    ProjectTopicName topicName;
    Publisher publisher = null;

    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "pubsub");
        channel.setMethodCallHandler(new PubsubPlugin());
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        String callStr = call.method;
        Map<String, String> map = (Map<String, String>) call.arguments;
        switch (callStr) {
            case "sendJson":
                result.success(sendJson(map));
                break;
            case "setProjectTopicName":
                result.success(setProjectTopicName(map));
                break;

        }
    }

    public int setProjectTopicName(Map<String, String> map) {
        String projectId = map.get("projectID");
        String topicId = map.get("topicID");
        topicName = ProjectTopicName.of(projectId, topicId);
        publisher = Publisher.newBuilder(topicName).build();
        return 0;
    }

    public int sendJson(Map<String, String> map) {
        String apiCall = map.get("apiCall");
        String where = map.get("where");
        String duration = map.get("duration");

        String jsonString = "";


        try {
            jsonString = new JSONObject()
                    .put("apiCall", apiCall)
                    .put("from", where)
                    .put("duration", duration).toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }

        try {
            // Create a publisher instance with default settings bound to the topic
            ByteString data = ByteString.copyFromUtf8(jsonString);
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

            // Once published, returns a server-assigned message id (unique within the topic)
            ApiFuture<String> future = publisher.publish(pubsubMessage);

            // Add an asynchronous callback to handle success / failure
            ApiFutures.addCallback(
                    future,
                    new ApiFutureCallback<String>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            if (throwable instanceof ApiException) {
                                ApiException apiException = ((ApiException) throwable);
                                // details on the API exception
                                System.out.println(apiException.getStatusCode().getCode());
                                System.out.println(apiException.isRetryable());
                            }
                            System.out.println("Error publishing message");
                        }

                        @Override
                        public void onSuccess(String messageId) {
                            // Once published, returns server-assigned message ids (unique within the topic)
                            System.out.println(messageId);
                        }
                    },
                    MoreExecutors.directExecutor()
            );

        } finally {
            if (publisher != null) {
                // When finished with the publisher, shutdown to free up resources.
                try {
                    publisher.shutdown();
                    publisher.awaitTermination(1, TimeUnit.MINUTES);
                } catch (Exception e) {
                    e.printStackTrace();
                    return -2;
                }
            }
            return 0;
        }
    }
}

