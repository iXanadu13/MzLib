package mz.mzlib.minecraft.mappings;

import mz.mzlib.util.RuntimeUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class Util
{
    public static String cache(File file, Supplier<String> supplier)
    {
        return new String(cache0(file, ()->supplier.get().getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }
    
    public static byte[] cache0(File file, Supplier<byte[]> supplier)
    {
        if(file==null)
            return supplier.get();
        if(file.isFile())
        {
            try(FileInputStream fis = new FileInputStream(file))
            {
                return readInputStream(fis);
            }
            catch(Throwable e)
            {
                throw RuntimeUtil.sneakilyThrow(e);
            }
        }
        else
        {
            byte[] result = supplier.get();
            boolean ignored = file.getParentFile().mkdirs();
            try(FileOutputStream fos = new FileOutputStream(file))
            {
                fos.write(result);
            }
            catch(Throwable e)
            {
                throw RuntimeUtil.sneakilyThrow(e);
            }
            return result;
        }
    }
    
    public static byte[] request0(String url)
    {
        return request0(url(url));
    }
    
    public static byte[] request0(URL url)
    {
        try(InputStream is = openConnectionCheckRedirects(url.openConnection()))
        {
            return readInputStream(is);
        }
        catch(Throwable e)
        {
            throw RuntimeUtil.sneakilyThrow(e);
        }
    }
    
    public static String request(String url)
    {
        return request(url(url));
    }
    
    public static String request(URL url)
    {
        return new String(request0(url), StandardCharsets.UTF_8);
    }
    
    public static URL url(String url)
    {
        try
        {
            return new URL(url);
        }
        catch(MalformedURLException e)
        {
            throw RuntimeUtil.sneakilyThrow(e);
        }
    }
    
    public interface ThrowableSupplier<T>
    {
        T get() throws Throwable;
    }
    
    public interface ThrowableRunnable
    {
        void run() throws Throwable;
    }
    
    public static byte[] readInputStream(InputStream inputStream)
    {
        byte[] buffer = new byte[4096];
        int len = 0;
        try(ByteArrayOutputStream bos = new ByteArrayOutputStream())
        {
            while((len = inputStream.read(buffer))!=-1)
            {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        }
        catch(Throwable e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static InputStream openConnectionCheckRedirects(URLConnection c) throws IOException
    {
        boolean redir;
        int redirects = 0;
        InputStream in;
        do
        {
            if(c instanceof HttpURLConnection)
            {
                ((HttpURLConnection)c).setInstanceFollowRedirects(false);
            }
            in = c.getInputStream();
            redir = false;
            if(c instanceof HttpURLConnection)
            {
                HttpURLConnection http = (HttpURLConnection)c;
                int stat = http.getResponseCode();
                if(stat>=300 && stat<=307 && stat!=306 && stat!=HttpURLConnection.HTTP_NOT_MODIFIED)
                {
                    URL base = http.getURL();
                    String loc = http.getHeaderField("Location");
                    URL target = null;
                    if(loc!=null)
                    {
                        target = new URL(base, loc);
                    }
                    http.disconnect();
                    if(target==null || redirects>=5)
                    {
                        throw new SecurityException("illegal URL redirect");
                    }
                    redir = true;
                    c = target.openConnection();
                    redirects++;
                }
            }
        }while(redir);
        return in;
    }
}
