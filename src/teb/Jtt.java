/*
 * $Copyright: copyright(c) 2007-2011 kuwata-lab.com all rights reserved. $
 * $License: Creative Commons Attribution (CC BY) $
 */
package teb;

import java.io.*;
import java.util.*;

import me.geso.jtt.JTTBuilder;
import me.geso.jtt.JTT;
import me.geso.jtt.InMemoryTemplateCache;
import me.geso.jtt.escape.Escaper;
import me.geso.jtt.escape.NullEscaper;

import java.nio.file.Paths;
import teb.model.Stock;
import teb.util.DoNothingOutputStream;

public class Jtt extends _BenchBase {

    private String template = "templates/";
    private final JTT jtt;
    
    public Jtt() {
        InMemoryTemplateCache cache = new InMemoryTemplateCache(InMemoryTemplateCache.CacheMode.CACHE_BUT_DO_NOT_CHECK_UPDATES);
        JTTBuilder builder = new JTTBuilder().addIncludePath(Paths.get(template)).setTemplateCache(cache);
        builder.setEscaper(new NullEscaper());
        this.jtt = builder.build();
    }
    
    protected void shutdown() {
    }

    @Override
    public void execute(Writer w0, Writer w1, int ntimes, List<Stock> items) throws Exception {
        String out;
        while (--ntimes >= 0) {
            out = render(items);
            
            if (ntimes == 0) {
                w1.write(out);
                w1.close();
            }
            else w0.write(out);
        }
    }
    
    @Override
    public void execute(OutputStream o0, OutputStream o1, int ntimes, List<Stock> items) throws Exception {
        String out;
        Writer w0 = new OutputStreamWriter(o0);
        Writer w1 = new OutputStreamWriter(o1);
        if (_BenchBase.bufferMode.get()) {
            w0 = new BufferedWriter(w0);
            w1 = new BufferedWriter(w1);
        }
        while (--ntimes >= 0) {
            out = render(items);
            if (ntimes == 0) {
                w1.write(out);
                w1.close();
            }
            else w0.write(out);
        }
        w0.close();
        w1.close();
    }

    @Override
    protected String execute(int ntimes, List<Stock> items) throws Exception {
        String output = null;
        while (--ntimes >= 0) {
            output = render(items);
        }
        return output;
    }

    private String render(List<Stock> items) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("items", items);

        return jtt.renderFile("stocks.jtt.html", params);
    }

    public static void main(String[] args) {
        new Jtt().run();
    }
}
