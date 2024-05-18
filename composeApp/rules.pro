# COMPOSE
-keepclassmembers class androidx.lifecycle.viewmodel.internal.JvmViewModelProviders {
    public <init>(...);
}
-dontnote androidx.compose.ui.text.platform.AwtFontUtils

# COM.MAYAKAPPS.COMPOSE.WINDOWSTYLER
-keep,includedescriptorclasses class com.mayakapps.compose.windowstyler.windows.jna.**

# IO.GITHUB.VINCEGLB.FILEKIT
-keep,includedescriptorclasses class io.github.vinceglb.filekit.core.platform.windows.jna.**

# JNA
-keep class com.sun.jna.** { *; }
-keepclassmembers class * extends com.sun.jna.** { public *; }
-keep class * implements com.sun.jna.** { *; }
-keepclassmembers class com.sun.jna.Native {
    public ** OPTIONS;
}
-keepclassmembers class com.sun.jna.ptr.ByReference {
    public ** getValue(...);
}
-keepclassmembers class org.slf4j.helpers.SubstituteLogger {
    public ** log(...);
}
# For native methods, see https://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * { native <methods>; }

# IMAGEIO
-keep,includedescriptorclasses class com.github.jaiimageio.** { *; }
-keep class org.apache.pdfbox.jbig2.** { *; }

# OPENCV
-keep class org.opencv.** { *; }
-dontwarn org.opencv.**

# JAVACPP, JAVACV, FFMPEG
-keep class org.bytedeco.** { *; }
-keepclassmembers class org.bytedeco.javacpp.indexer.UnsafeRaw {
    public byte getByte(...);
    public int getInt(...);
    public long getLong(...);
    public float getFloat(...);
    public double getDouble(...);
    public char getChar(...);
    public short getShort(...);
    public int arrayBaseOffset(...);
}
-keepclassmembers class org.bytedeco.javacpp.tools.CacheMojo {
    public ** cachePackage(...);
    public ** load(java.lang.Class[]);
}
-keepclassmembers class org.bytedeco.javacpp.tools.Generator {
    public ** value(...);
    public ** value;
}
-keepclassmembers class org.bytedeco.javacv.CameraDevice$SettingsImplementation {
    public ** getDeviceDescriptions();
}
-keepclassmembers class org.bytedeco.javacv.FrameGrabber {
    public ** tryLoad();
    public ** getDeviceDescriptions();
}
-keepclassmembers class org.bytedeco.javacv.FrameRecorder {
    public ** tryLoad();
}
-dontwarn org.bytedeco.**

# REFLECTION
-keepattributes *Annotation*,Annotation,Signature,InnerClasses,EnclosingMethod,LocalVariableTable

# ENUM
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# COIL3
-keep,includedescriptorclasses class coil3.* { *; }
-keep class * extends coil3.util.DecoderServiceLoaderTarget { *; }

# KOTLINX.DATETIME.INSTANT
-keep class kotlinx.datetime.Instant { *; }

# KOTLINX.SERIALIZATION
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class kotlinx.serialization.internal.PlatformKt {
    public ** INSTANCE;
}
-keepnames class kotlinx.serialization.internal.** { *; }

# COROUTINES
# Reference: https://github.com/Kotlin/kotlinx.coroutines/blob/13f27f729547e5c22d17d5b5de3582d450b037b4/kotlinx-coroutines-core/jvm/resources/META-INF/com.android.tools/proguard/coroutines.pro
# ServiceLoader support for kotlinx.coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keep class kotlinx.coroutines.sync.Mutex { *; }
-keep class kotlinx.coroutines.flow.FlowCollector { *; }
-keep class * implements kotlinx.coroutines.internal.MainDispatcherFactory
-keep class * implements kotlinx.coroutines.CoroutineExceptionHandler
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-keepclassmembers class kotlin.coroutines.SafeContinuation {
    volatile <fields>;
}

# ANDROIDX.ROOM
# Reference: https://github.com/ryanw-mobile/OctoMeter/blob/362700134979fbcb80336a4caead6ddb5d2ff097/composeApp/compose-desktop.pro
# Room persistence library
-keep class androidx.room.** { *; }
-keep class androidx.sqlite.db.** { *; }
-keep class androidx.sqlite.** { *; }
# Keep generated Room classes
-keepclassmembers class * {
    @androidx.room.* <fields>;
    @androidx.room.* <methods>;
}
# Keep the implementation of the Room database
-keep class * extends androidx.room.RoomDatabase { *; }
# Keep the DAO classes
-keep interface * extends androidx.room.RoomDatabase { *; }
-keep,includedescriptorclasses class io.github.sadellie.indexxo.core.database.** { *; }
-keepclassmembers class androidx.room.util.KClassUtil {
    public <init>(...);
}

# ANDROIDX.DATASTORE
-dontnote androidx.datastore.**

# TIKA
-dontwarn org.apache.commons.**
-dontwarn org.apache.logging.log4j.**
-dontwarn org.apache.pdfbox.tools.PDFBox
-dontwarn org.apache.poi.xslf.**
-dontwarn org.apache.tika.**
-dontwarn org.apache.poi.**
-dontwarn org.apache.xerces.**
-dontwarn org.etsi.uri.**
-dontwarn org.openxmlformats.**
-dontwarn org.apache.xmlbeans.**
-dontwarn org.bouncycastle.mail.smime.**
-dontwarn org.codelibs.jhighlight.servlet.**
-dontwarn org.jdom2.xpath.jaxen.**
-dontwarn net.bytebuddy.**
-dontwarn java.util.prefs.**
-dontwarn javax.imageio.metadata.**
-dontwarn javax.swing.plaf.synth.SynthParser
-dontwarn javax.xml.catalog.**
-dontwarn javax.xml.crypto.dom.DOMCryptoContext
-dontwarn javax.xml.crypto.dom.DOMStructure
-dontwarn javax.xml.crypto.dsig.dom.DOMSignContext
-dontwarn javax.xml.crypto.dsig.dom.DOMValidateContext
-dontwarn jdk.xml.internal.**
-dontwarn com.healthmarketscience.jackcess.crypt.util.RC4EngineLegacy
-dontwarn com.sun.**
-dontwarn com.jmatio.io.MatFileReader
-dontwarn com.microsoft.**
-dontwarn org.w3.x2000.**
-keep,includedescriptorclasses class net.bytebuddy.** { *; }
-keep,includedescriptorclasses class org.apache.** { *;}
-keep,includedescriptorclasses class org.bouncycastle.** { *;}
-keep,includedescriptorclasses class org.xml.** { *; }
-keep,includedescriptorclasses class javax.xml.** { *; }

# COM.TWELVEMONKEYS.IMAGEIO
-dontwarn org.apache.batik.**
-dontwarn com.twelvemonkeys.**
-keep,includedescriptorclasses class com.twelvemonkeys.** { *; }
