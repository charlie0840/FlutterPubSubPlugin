import 'dart:async';

import 'package:flutter/services.dart';

class Pubsub {
  static const MethodChannel _channel =
      const MethodChannel('pubsub');

  static Future<int> setProjectTopicName(String projectID, String topicID) async {
    Map<String, String> map = new Map();
    map.putIfAbsent("projectID", ()=>projectID);
    map.putIfAbsent("topicID", ()=>topicID);
    int res = await _channel.invokeMethod('setProjectTopicName', map);
    return res;
  }

  static Future<int> sendJson(Map<String, String> map) async {
    final int res = await _channel.invokeMethod('sendJson', map);
    return res;
  }
}
