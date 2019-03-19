import 'dart:async';

import 'package:flutter/services.dart';

class AliPay {
  final MethodChannel _channel = const MethodChannel('BaiduMapPlugin');

  Future<Null> gpsGetOnce() async {
    return await _channel.invokeMethod('gpsGetOnce');
  }
  Future<Null> gpsStart(int time) async {
    return await _channel.invokeMethod('gpsStart',time);
  }
  Future<Null> gpsStop() async {
    return await _channel.invokeMethod('gpsStop');
  }
  Future<Null> clear() async {
    return await _channel.invokeMethod('clear');
  }
}

class BDLocation{
  final String time;
  final int errCord;
  final double X;
  final double Y;
  final double R;
  final String addrStr;
  final String cityCode;
  final String city;

  const BDLocation({this.time})



}
