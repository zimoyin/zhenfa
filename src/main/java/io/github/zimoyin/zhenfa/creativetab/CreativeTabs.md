```java
public class CreativeTabs {
    // 创建一个 CreativeModeTab
    public static final CreativeModeTab TestCreativeTab = CreativeModeTabBuilder.builder("test")
            .lang(Lang.of(Lang.LangType.ZH_CN, "测试标签"))
            .build();
    // 简单的创建一个 CreativeModeTab
    public static final CreativeModeTab TestCreativeTab2 = CreativeModeTabBuilder.create("test2");
}
```