# Keep rules for realtime dependencies
-keep class io.socket.** { *; }
-keep class org.greenrobot.eventbus.** { *; }
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
