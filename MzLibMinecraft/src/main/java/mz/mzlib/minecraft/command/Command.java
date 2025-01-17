package mz.mzlib.minecraft.command;

import mz.mzlib.minecraft.entity.player.EntityPlayer;
import mz.mzlib.minecraft.permission.Permission;
import mz.mzlib.minecraft.permission.PermissionHelp;
import mz.mzlib.minecraft.text.Text;
import mz.mzlib.util.CollectionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Command
{
    public String namespace ="minecraft";
    public String name;
    public String[] aliases;
    public Function<CommandSender, Text> permissionChecker;
    public Consumer<CommandContext> handler;
    
    public Command(String name, String ...aliases)
    {
        this.name = name;
        this.aliases = aliases;
    }
    
    public Command setNamespace(String value)
    {
        this.namespace =value;
        return this;
    }
    
    public List<Command> children=new ArrayList<>();
    
    public Command addChild(Command child)
    {
        this.children.add(child);
        return this;
    }
    
    public void removeChild(Command child)
    {
        this.children.remove(child);
    }
    
    public Command setPermissionChecker(Function<CommandSender, Text> value)
    {
        this.permissionChecker = value;
        return this;
    }
    
    @SafeVarargs
    public final Command setPermissionCheckers(Function<CommandSender, Text>... value)
    {
        return this.setPermissionChecker(sender->
        {
            for(Function<CommandSender, Text> i:value)
            {
                Text result = i.apply(sender);
                if(result!=null)
                    return result;
            }
            return null;
        });
    }
    
    public static Text checkPermissionAnd(Text ...permissionCheckInfos)
    {
        for(Text i:permissionCheckInfos)
            if(i!=null)
                return i;
        return null;
    }
    
    public static Text checkPermissionSenderPlayer(CommandSender sender)
    {
        if(!sender.isInstanceOf(EntityPlayer::create))
            return Text.literal("§4只有玩家能执行该命令"); // TODO: i18n
        return null;
    }
    
    public static Text checkPermission(CommandSender sender, Permission permission)
    {
        if(PermissionHelp.instance.check(sender, permission))
            return null;
        return Text.literal("§4需要权限"+permission.id); // TODO: i18n
    }
    
    public static Function<CommandSender, Text> permissionChecker(Permission permission)
    {
        return sender->checkPermission(sender, permission);
    }
    
    public Command setHandler(Consumer<CommandContext> value)
    {
        this.handler = value;
        return this;
    }
    
    public List<String> suggest(CommandSender sender, String command, String args)
    {
        Text permissionCheckInfo=this.permissionChecker!=null?this.permissionChecker.apply(sender):null;
        if(permissionCheckInfo!=null)
            return CollectionUtil.newArrayList(permissionCheckInfo.toLiteral());
        String[] argv2 = args.split("\\s+", 2);
        if(argv2.length>1)
            for(Command i:this.children)
            {
                if(CollectionUtil.addAll(CollectionUtil.newArrayList(i.aliases), i.name).contains(argv2[0]))
                    return i.suggest(sender, command+' '+argv2[0], argv2[1]);
            }
        List<String> result=new ArrayList<>();
        if(this.handler!=null)
        {
            CommandContext context = new CommandContext(sender, command, args, false);
            this.handler.accept(context);
            result.addAll(context.suggestions);
        }
        if(argv2.length==1)
        {
            for(Command i:this.children)
            {
                if(i.name.startsWith(argv2[0]))
                    result.add(i.name);
            }
            for(Command i:this.children)
                for(String j:i.aliases)
                {
                    if(j.startsWith(argv2[0]))
                        result.add(j);
                }
        }
        return result;
    }
    public void execute(CommandSender sender, String command, String args)
    {
        Text permissionCheckInfo=this.permissionChecker!=null?this.permissionChecker.apply(sender):null;
        if(permissionCheckInfo!=null)
        {
            sender.sendMessage(permissionCheckInfo);
            return;
        }
        String[] argv2 = args.split("\\s+", 2);
        for(Command i:this.children)
        {
            if(CollectionUtil.addAll(CollectionUtil.newArrayList(i.aliases), i.name).contains(argv2[0]))
            {
                i.execute(sender, command+' '+argv2[0], argv2.length>1?argv2[1]:"");
                return;
            }
        }
        CommandContext context = new CommandContext(sender, command, args, true);
        if(this.handler!=null)
        {
            this.handler.accept(context);
        }
        if(this.handler==null || !context.successful)
        {
            for(String i:context.suggestions)
                sender.sendMessage(Text.literal(i));
            // TODO: i18n
            if(!this.children.isEmpty())
                sender.sendMessage(Text.literal("/"+command+" <"+String.join(" | ", this.children.stream().map(i->i.name).collect(Collectors.toSet()))+"> ..."));
            if(this.handler!=null)
                sender.sendMessage(Text.literal("/"+command+" "+String.join(" ", context.argNames.stream().map(i->"<"+i+">").collect(Collectors.toSet()))));
        }
    }
}
