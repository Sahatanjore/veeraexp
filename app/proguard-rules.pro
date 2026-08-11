# Room
-keep class androidx.room.** { *; }
-keep @androidx.room.Entity class * { *; }

# Keep data classes used by Room
-keepclassmembers class com.veeraexp.app.data.entity.** { *; }
