-dontwarn com.fasterxml.jackson.core.**
-dontwarn com.google.common.annotations.**
-dontwarn javax.ws.rs.**
-dontwarn javax.annotation.**
-dontwarn org.immutables.value.**

# R8 GSON special rules :
# https://r8.googlesource.com/r8/+/refs/heads/master/compatibility-faq.md => Troubleshooting
# For data classes used for serialization all fields that are used in the serialization must be kept by the configuration. R8 can decide to replace instances of types that are never instantiated with null. So if instances of a given class are only created through deserialization from JSON, R8 will not see that class as instantiated leaving it as always null.
# If the @SerializedName annotation is not used the following conservative rule can be used for each data class :

-keepclassmembers class com.lolo.io.onelist.core.model.ItemList {
 !transient <fields>;
}
-keepclassmembers class com.lolo.io.onelist.core.model.Item {
 !transient <fields>;
}

# GSON uses type tokens to serialize and deserialize generic types.
# The anonymous class will have a generic signature argument of List<String> to the super type TypeToken that is reflective read for serialization. It is therefore necessary to keep both the Signature attribute, the com.google.gson.reflect.TypeToken class and all sub-types.

-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * implements java.lang.reflect.Type
