import 'dart:async';

import 'package:flutter/services.dart';

class Pubsub {
  static const MethodChannel _channel =
      const MethodChannel('pubsub');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<Null> showToast(String msg) async {
    await _channel.invokeMethod('showToast');
  }

  static Future<int> sendJson(Map<String, String> map) async {
    final int res = await _channel.invokeMethod('sendJson', map);
    return res;
  }
}
