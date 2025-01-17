package mz.mzlib.minecraft.ui.book;

import mz.mzlib.minecraft.MinecraftServer;
import mz.mzlib.minecraft.MzLibMinecraft;
import mz.mzlib.minecraft.SleepTicks;
import mz.mzlib.minecraft.command.Command;
import mz.mzlib.minecraft.command.argument.ArgumentParserInt;
import mz.mzlib.minecraft.entity.player.EntityPlayer;
import mz.mzlib.minecraft.item.ItemStack;
import mz.mzlib.minecraft.item.ItemStackBuilder;
import mz.mzlib.minecraft.item.ItemWrittenBook;
import mz.mzlib.minecraft.text.Text;
import mz.mzlib.minecraft.text.TextClickEvent;
import mz.mzlib.minecraft.ui.UI;
import mz.mzlib.minecraft.ui.UIStack;
import mz.mzlib.module.MzModule;
import mz.mzlib.util.async.AsyncFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class UIWrittenBook implements UI
{
    public abstract List<Text> getPages(EntityPlayer player);
    
    public List<Consumer<EntityPlayer>> buttons = new ArrayList<>();
    
    public int newButton(Consumer<EntityPlayer> handle)
    {
        this.buttons.add(handle);
        return this.buttons.size()-1;
    }
    
    public static Text setButton(Text text, int button)
    {
        return text.setClickEvent(TextClickEvent.newInstance(TextClickEvent.Action.runCommand(), "/"+MzLibMinecraft.instance.command.name+" "+Module.instance.command.name+" "+button));
    }
    
    public void clear()
    {
        this.buttons.clear();
    }
    
    @Override
    public void open(EntityPlayer player)
    {
        ItemStack book = new ItemStackBuilder("written_book").build();
        ItemWrittenBook.setPages(book, getPages(player));
        player.openBook(book);
        new AsyncFunction<Void>()
        {
            @Override
            public void run()
            {
            }
            @Override
            protected Void template() throws Throwable
            {
                await(new SleepTicks(50));
                UIWrittenBook.this.close(player);
                return null;
            }
        }.start(MinecraftServer.instance);
    }
    
    public static class Module extends MzModule
    {
        public static Module instance = new Module();
        
        public Command command;
        
        @Override
        public void onLoad()
        {
            MzLibMinecraft.instance.command.addChild(this.command = new Command("book_click").setPermissionCheckers(Command::checkPermissionSenderPlayer, sender->UIStack.get(sender.castTo(EntityPlayer::create)).top() instanceof UIWrittenBook ? null : Text.literal(/*TODO i18n*/"§4You are not opening a book UI.")).setHandler(context->
            {
                Integer button = new ArgumentParserInt("button").handle(context);
                if(!context.successful || !context.doExecute)
                    return;
                List<Consumer<EntityPlayer>> buttons = ((UIWrittenBook)UIStack.get(context.sender.castTo(EntityPlayer::create)).top()).buttons;
                if(button<0 || button>=buttons.size())
                {
                    context.successful = false;
                    context.suggestions.add("§4Invalid button number");
                    return;
                }
                buttons.get(button).accept(context.sender.castTo(EntityPlayer::create));
            }));
        }
        
        @Override
        public void onUnload()
        {
            MzLibMinecraft.instance.command.removeChild(this.command);
        }
    }
}
