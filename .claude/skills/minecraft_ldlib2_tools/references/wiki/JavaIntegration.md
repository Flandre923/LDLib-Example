Java Integration
Since 2.0.0

maven
You can find the latest version from our maven.

ldlib2 maven


repositories {
    // LDLib2
    maven { url = "https://maven.firstdark.dev/snapshots" } 
}

dependencies {
    // LDLib2
    implementation("com.lowdragmc.ldlib2:ldlib2-neoforge-${minecraft_version}:${ldlib2_version}:all") { transitive = false }
    compileOnly("org.appliedenergistics.yoga:yoga:1.0.0")   
}
IDEA Plugin - LDLib Dev Tool
Image title

If you are going to develop with LDLib2, we strongly recommend you to install our IDEA Plugin LDLib Dev Tool. The plugin has:

code highlight
syntax check
cdoe jumping
auto complete
others
which greatly assist you in utilizing features of LDLib2. Especially, all the annotations of LDLib2 have been supported for use.

LDLibPlugin
You can create a LDLibPlugin by using ILDLibPlugin and @LDLibPlugin
你可以用 ILDLibPlugin 和 @LDLibPlugin 来创建 LDLibPlugin


@LDLibPlugin
public class MyLDLibPlugin implements ILDLibPlugin {
    public void onLoad() {
        // do your register or setup for LDLib2 here.
    }
}