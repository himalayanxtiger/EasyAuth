# Add any ProGuard configurations specific to this
# extension here.

-keep public class com.xtiger.easyauth.EasyAuth {
    public *;
 }
-keeppackagenames gnu.kawa**, gnu.expr**

-optimizationpasses 4
-allowaccessmodification
-mergeinterfacesaggressively

-repackageclasses 'com/xtiger/easyauth/repack'
-flattenpackagehierarchy
-dontpreverify
