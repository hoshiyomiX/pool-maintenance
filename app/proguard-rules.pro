# ============================================================
# Pool Maintenance App — ProGuard / R8 Rules
# ============================================================

# ---------- Room ----------
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# ---------- Hilt / Dagger ----------
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class *
-keepclasseswithmembers class * {
    @javax.inject.Inject <init>(...);
}

# ---------- Compose ----------
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ---------- Kotlin / Coroutines ----------
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ---------- Serializable / Parcelable ----------
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# ---------- App-specific ----------
-keep class com.poolmaintenance.app.data.** { *; }
-keep class com.poolmaintenance.app.ui.** { *; }
-keep class com.poolmaintenance.app.PoolMaintenanceApp { *; }

# ---------- Enum ----------
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ---------- General ----------
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes EnclosingMethod
