## block 层
block 层是用于定义各种方块的，该层次中通过指定的方式定义 block 会自动的扫描并注册到系统中，这样就会很方便的解决各种问题也会解决代码颜值问题。  

## 定义一个 Block 
在 `block.base` 软件包中存在 `BaseBlock` 类，该类封装一些方法，并且一般将该类作为MOD中所有 Block 的父类。  

当然你也可以继承 MC 中的 `Block` 类这不会导致任何问题的发生。

### 继承 `BaseBlock` 或者 `Block` 类  

继承后会要求你实现构造方法，但是这里你必须提供一个空构造方法，之后该空构造方法将会被反射框架调用并进行自动注册
```java
public class TestBlock extends BaseBlock {
    public TestBlock() {
        super(Properties.of(Material.STONE)
                .strength(1.5f)
                .requiresCorrectToolForDrops()
        );
    }   
}
```


### 注解标注注册类

当你完成第一个**继承Block类**后你就需要标记类是需要让框架进行托管的。  
注解有许多参数，但是默认你需要提供方块ID参数，否则他将使用类名作为ID
```java
// 定义方块的ID为 test ，如果你留空则使用类名作为ID。ID不能使用大写字母,并且使用小写字母开头，也不能包含特殊符号
@BlockRegterTables.RegisterBlock("test")
public class TestBlock extends BaseBlock {
    public TestBlock() {
        super(Properties.of(Material.STONE)
                .strength(1.5f) // 设置硬度
                .requiresCorrectToolForDrops() // 必须使用对的工具才能破坏方块
        );
    }
}
```
到这呢就可以启动游戏并在建筑列表里面找到你的方块了，但是此时方块还没有贴图，这时候你就需要准备一个贴图。   
放到资源文件夹中如： `resources\[你的MOD ID]\textures\block\[方块ID].png` 这样你就会发现背包中的方块有贴图了，并且放在地上也有贴图。  
如果你想要为你的方块物品添加另外的贴图不使用六边形贴图你就需要将贴图放到 `resources\[你的MOD ID]\textures\item\[方块ID].png` 中此时手上的方块物品贴图就会被替换  

为什么方块物品的贴图需要放到 item 的文件夹下？  
这是因为注册一个方块需要分别注册方块(Block)和方块物品(BlockItem), 只有这样方块才能被拿在手上放在物品栏里，否则方块是不会变成物品的。

### 注入Data字段
在不使用本框架的情况下，注册物品通常会产生 RegistryObject<Block> 和  RegistryObject<BlockItem> 这两个对象, 是将方块和物品放在了合适的实际进行创建对象并管理。  
如果你想要获取到这两个对象，你需要在类中创建一个静态的 `Data` 字段，然后使用 `Data` 字段获取到方块物品对象。
```java
// 设置  data 为 true 后才会进行注入
@BlockRegterTables.RegisterBlock(value = "test",data = true)
public class TestBlock extends BaseBlock {
    // 等待注入，字段名称任意即可
    public static BaseItem.Data RegisterItemData = null;
    
    public TestBlock() {
        super(Properties.of(Material.STONE)
                .strength(1.5f) // 设置硬度
                .requiresCorrectToolForDrops() // 必须使用对的工具才能破坏方块
        );
    }
}
```


### [可读]为方块自定义方块物品实例

通常不需要对方块物品进行特殊处理，除了你需要将方块放在其他的创造标签页中，默认的会将方块放在方块物品标签页中。
```java
@BlockRegterTables.RegisterBlock("test")
public class TestBlock extends BaseBlock {
    public TestBlock() {
        super(Properties.of(Material.STONE)
                .strength(1.5f) // 设置硬度
                .requiresCorrectToolForDrops() // 必须使用对的工具才能破坏方块
        );
    }
    
    /**
     * 设置方块物品。重写该方法修改方块物品的属性
     */
    @Override
    public BlockItem getBlockItem() {
        return new BlockItem(this, new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));
    }
}
```

### [可读]为方块添加破坏等级(废弃的方法)
为方块添加破坏等级呢在旧的版本中可以通过方法直接设置，但是在这个版本中就需要为方块设置标签来告诉程序只有这个标签的物品才能破坏方块。  
但是在这里不使用标签方式，使用自实现的通过方法的形式设置，但是需要注意的是该方法是不推荐使用的。
```java
@BlockRegterTables.RegisterBlock("test")
public class TestBlock extends BaseBlock {
    public TestBlock() {
        super(Properties.of(Material.STONE)
                .strength(1.5f) // 设置硬度
                .requiresCorrectToolForDrops() // 必须使用对的工具才能破坏方块
        );
        
        // 只有同时设置以下两个方法才会生效
        // 为了让他们生效，BaseBlock 重写了 `canHarvestBlock` 和 `defaultDestroyTime` 方法
        setHarvestLevel(3); // 设置挖掘等级，3为钻石稿子
        setToolType(ToolType.PICKAXE); // 设置挖掘工具类型，PICKAXE为镐子
    }
}
```

### 创建一个实体方块
> **引用：**_[方块实体](https://www.teacon.cn/xiaozhong/1.18.x/block-entity)_    
> 
> Minecraft 使用名为方块实体（Block Entity）的机制来实现一些一般情况下方块做不到或很难做到的事情，例如
> 持有更复杂的数据（物品、文本等）
> 实现像熔炉那样持续不断地行为
> 拥有奇妙的渲染特效

在本框架中你会看到 BlockEntity 和 EntityBlock。这里为需要区分下，BlockEntity 是一个实体即数据的载体，而 EntityBlock 是一个方块。  
同样的注册一个 EntityBlock 需要注册三个东西 方块(Block)、方块实体(BlockEntity)、方块物品(BlockItem) 这些和注册方块一样，仅需要一个注解即可  
```java
// 这里需要在注解指定方块实体的实现类
@BlockRegterTables.RegisterBlock(value = "test", blockEntity = TestBlock.TestBlockEntity.class)
public class TestBlock extends BaseEntityBlock {
    public Test2Block() {
        super(Properties.of(Material.STONE)
                .strength(1.5f)
                .requiresCorrectToolForDrops()
        );
    }

    /**
     * 方块实体
     */
    public static class TestBlockEntity extends BaseBlockEntity {
        // 这个构造方法是必须的，直接抄即可
        public Test2BlockEntity(BlockPos worldPosition, BlockState blockState) {
            super(getEntityType(TestBlockEntity.class), worldPosition, blockState);
        }
    }
}
```
实例：右键这个方块的时候会调用递增代码。  
_注意：方块实体需要考虑服务器与客户端的通讯，放在多人游戏中，方块数据不同步_
> 两个类中均有 `serverTick` 和 `clientTick` 的方法，根据需要自行覆盖重写
```java
@BlockRegterTables.RegisterBlock(value = "test", blockEntity = TestBlock.TestBlockEntity.class)
public class TestBlock extends BaseEntityBlock {
    public Test2Block() {
        super(Properties.of(Material.STONE)
                .strength(1.5f)
                .requiresCorrectToolForDrops()
        );
    }


    /**
     * 右键这个方块的时候会调用
     */
    @Override
    public @NotNull InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.getBlockEntity(pPos) instanceof Test2BlockEntity tbe) {
            tbe.use(pPlayer);
            pPlayer.sendMessage(new TextComponent("使用方块的手：" + pHand.name()), pPlayer.getUUID());
        }

        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    /**
     * 方块实体
     */
    public static class Test2BlockEntity extends BaseBlockEntity {
        /**
         * 必须要有这个构造方法
         */
        public Test2BlockEntity(BlockPos worldPosition, BlockState blockState) {
            // getEntityType(Test2BlockEntity.class) 是必须的
            super(getEntityType(Test2BlockEntity.class), worldPosition, blockState);
        }

        int count;

        boolean initialized = false;
        int serverMsg;
        int clientMsg;

        public void use(Player player) {
            if (getLevel().isClientSide()) {
                if (!initialized) {
                    clientMsg = 1001;
                    initialized = true;
                }

                // 调试信息，展示了不同步状态下的信息
                player.sendMessage(new TextComponent("仅在客户端的内容：" + clientMsg), player.getUUID());
                player.sendMessage(new TextComponent("双端内容客户端：" + count), player.getUUID());
            } else {
                if (!initialized) {
                    serverMsg = 2332;
                    initialized = true;
                }

                // 调试信息，展示了不同步状态下的信息
                player.sendMessage(new TextComponent("仅在服务器端的内容：" + serverMsg), player.getUUID());

                count++;
                player.sendMessage(new TextComponent("双端内容服务端：" + count), player.getUUID());

                sync();
            }
        }

        /**
         * 同步的方法，看不懂就照抄，调用它会调用下面的 getUpdateTag
         * 同步操作 ：
         *      调用 sync() 方法发送数据包到客户端，更新客户端的数据（如 count）。
         *      sync() 内部通过 ClientboundBlockEntityDataPacket 实现网络同步。
         * 状态重置 ：同步完成后将 needSync 设为 false，避免重复同步。
         */
        protected void sync() {
            if (level != null && !level.isClientSide) {
                ClientboundBlockEntityDataPacket p = ClientboundBlockEntityDataPacket.create(this);
                ((ServerLevel) this.level).getChunkSource().chunkMap.getPlayers(new ChunkPos(getBlockPos()), false)
                        .forEach(k -> k.connection.send(p));

                // 用来告知 mc，“这个方块得保存”的东西，你也可以在其他地方调用
                setChanged();
            }
        }

        /**
         * 网络包会调用这个方法（服务端）
         */
        @Override
        public CompoundTag getUpdateTag() {
            CompoundTag result = new CompoundTag();

            result.putInt("count", count);

            return result;
        }

        /**
         * 持久化会调用这个方法：也就是说，你退出游戏再进入游戏，数据不会消失就是它的作用（服务端）
         */
        @Override
        protected void saveAdditional(CompoundTag pTag) {
            pTag.putInt("count", count);
        }

        /**
         * 不论是网络包还是持久化都会调用这个方法，从 nbt 加载数据（服务端（持久化）/客户端（网络包））
         * 注意，此时的 BlockEntity *没有* 初始化 level，所以此时 getLevel 一定会报错
         * 但服务端的内容不会自动同步到客户端，怎么办呢？见下方的解决方法
         */
        @Override
        public void load(CompoundTag pTag) {
            super.load(pTag);

            count = pTag.getInt("count");

            // 虽然此时的客户端上的 needSync 也被修改了，但由于这个字段不会在客户端使用所以无需担心
            needSync = true;
        }


        /**
         * 每 tick 都会调用，仅在服务端上执行
         */
        @Override
        public void serverTick(Level level, BlockPos pos, BlockState state, BlockEntity e) {
            asEntity(this.getClass(),e).ifPresent(Test2BlockEntity::syncTick);
        }


        // 同步的解决方案

        boolean needSync;

        /**
         * 数据同步需求 ：服务端数据（如 count 字段）需要同步到客户端，但直接操作可能引发线程安全或状态不一致的问题。
         * needSync 标志 ：用于标记是否需要触发同步。
         */
        void syncTick() {
            if (needSync) {
                sync();
                needSync = false;
            }
        }
    }
}
```

### 自动生成JSON
在1.12 版本中，方块物品等模型都需要手动写Json 来实现。但是在 1.18 中有 capability 来进行代码自动化的生成。  
自动化生成需要调用 gradle 命令 `./gradlew runData`。但是这不意味着所有的一切都会被生成，需要手动在代码中进行设置一部分内容。  
在代码中设置内容的目的在于手动配置JSON过于繁琐并且可能配置错误，因此代码生成的方式可以极大的保证正确率。  
使用方式呢也是需要注解的, 在注解中指定生成器  
```java
@BlockRegterTables.RegisterBlock(value = "test", data = true, generatedData = TestBlock.GeneratedData.class)
public class TestBlock extends BaseBlock {
    // 会在合适的时间注入
    public static BaseBlock.Data RegisterBlockData = null;

    public TestBlock() {
        super(Properties.of(Material.STONE)
                .strength(1.5f)
                .requiresCorrectToolForDrops()
        );
        setBlockName("Test");
    }


    /**
     * 继承的 JSON 生成器。
     * 如果需要配置什么，就在 GeneratedData 中重写父类的方法即可。
     * * tags 方法设置方块的标签
     * * lang 方法设置方块的文本用于本地化/国际化
     * * registerStatesAndModel 注册方块的方向和模型（默认使用无状态模型）
     * * registerBlockItemRecipe 方法设置方块物品的配方（默认为空）
     * * registerLootTable 方法设置方块的掉落物（默认为方块物品，即挖掉方块掉落方块的Item）
     * * registerItemModel 方法设置方块物品的模型（默认为方块物品的模型如果不存在则使用方块六边模型）
     * * itemTags 方法设置方块物品的标签（默认为方块的标签既空）
     */
    public static class GeneratedData extends BaseGeneratedBlockData {
        public GeneratedData(Data data) {
            super(data);
        }

        @Override
        public List<TagKey<Block>> tags() {
            // 为方块设置标签
            // BlockTags.MINEABLE_WITH_PICKAXE 挖掘需要使用镐子
            // BlockTags.NEEDS_IRON_TOOL 挖掘需要铁工具以及更高等级的稿子
            return List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
        }

        @Override
        public List<Lang> lang() {
            return List.of(new Lang(Lang.LangType.ZH_CN,"简单测试方块"));
        }
    }
}
```

## 定义一堆方块
如果为每个方块都创建一个类的话，工作量太多了，类也太多了，因此具有相同特性的方块可以统一创建。  
在这里仅仅使用演示创建无特殊功能的方块。即所有的创建都是基于 BaseBlock
```java
// 没有该注解程序无法扫描到此类就无法加载类无法执行 Static
@BlockRegterTables.RegisterBlock(isRegister = false)
public class Test4Blocks {
    // register 有众多重载，除了 register(Class) 是自动调用以外其他的都是可以调用的
    // 不是通过 register(Class) 注册的无法通过 getData(cls) 获取数据。但是 getDataList 还是会包含
    public static final BaseBlock.Data TEST_BLOCK_DATA = BlockRegterTables.register("ctestblock4");
    // 注册一个带有 generatedData class 的注册方式
    public static final BaseBlock.Data TEST_BLOCK_DATA2 = BlockRegterTables.register("ctestblock5",  Test4GeneratedBlockData.class);
    // 注册一个带有 generatedData instance 的注册方式
    public static final BaseBlock.Data TEST_BLOCK_DATA3 = BlockRegterTables.register("ctestblock6", data -> new BaseGeneratedBlockData(data){
        @Override
        public List<Lang> lang() {
            return List.of(new Lang(Lang.LangType.ZH_CN,"测试独立注册 id3"));
        }
    });



    public static class Test4GeneratedBlockData extends BaseGeneratedBlockData {
        public Test4GeneratedBlockData(BaseBlock.Data data) {
            super(data);
        }

        @Override
        public List<Lang> lang() {
            // 可以为多个方块设置语言
            return List.of(
                    new Lang(Lang.LangType.ZH_CN, TEST_BLOCK_DATA.getBlock(), "测试集体注册 id1"),
                    new Lang(Lang.LangType.ZH_CN, TEST_BLOCK_DATA2.getBlock(), "测试集体注册 id2")
            );
        }

        @Override
        public List<TagKey<Block>> tags() {
            // 注册标签需要石头稿子及以上才能挖掘
            return List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL);
        }
    }
}
```
如果你需要特殊的可以使用

```
@BlockRegterTables.RegisterBlock(isRegister = false)
public class Test4Blocks {
    // 可以自行的创建需要的 Block 和 Item 同样提供多种重载
    // 对于实体方块则不支持这种创建方式
    public static final BaseBlock.Data TEST_BLOCK_DATA3 = BlockRegterTables.register("ctestblock6", () -> new BaseBlock(...){...},()->new BlockItem(...){...}, data -> new BaseGeneratedBlockData(data) {
        // ...
    });
}
```

## 我想手动创建不想使用框架提供的自动化
参考:
* io.github.zimoyin.zhenfa.block.base.BlockRegterTables.BLOCKS
* io.github.zimoyin.zhenfa.block.base.BlockRegterTables.BLOCK_ENTITIES
* io.github.zimoyin.zhenfa.item.base.ItemRegterTables.ITEMS
另外你可能需要在 autoRegisterAll 执行前完成注册