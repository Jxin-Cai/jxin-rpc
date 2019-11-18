package com.jxin.rpc.core.util.serializer;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 蔡佳新
 * @version 1.0
 * @since 2019/11/18 19:27
 */
public class SerializeUtilTest {
    private static final String KEY_STR = "body=";
    private static final String PATH = "C:\\Users\\admin\\Desktop\\demo";
    private static final List<byte[]> EMPTY_BYTE_LIST = Lists.newArrayList();
    private static final byte[] EMPTY_BYTE_ARR = {};
    @Test
    public void serialize() {
        final List<Byte> bytes = GsonUtil.GsonToList("[4, 0, 0, 0, 2, 11, 12]", byte.class);
        System.out.println(bytes.toArray());
    }

    @Test
    public void parse() {
        final File file = new File(PATH);
        Arrays.stream(file.listFiles()).forEach(file1 -> {
            final List<byte[]> bytes = allByteArr(file1);
            if(CollectionUtils.isNotEmpty(bytes)){
                final List<String> collect = bytes.stream()
                                                  .filter(byt -> byt.length > 0)
                                                  .map(SerializeUtil::parse)
                                                  .map(GsonUtil::GsonToStr)
                                                  .collect(Collectors.toList());
                final FileWriter writer = new FileWriter(PATH + "\\"+ file1.getName() + ".translate");
                writer.writeLines(collect);

            }
        });
    }
    private List<byte[]> allByteArr(File file){
        final FileReader fileReader = new FileReader(file);

        final List<String> lines = fileReader.readLines();
        if(CollectionUtils.isEmpty(lines)){
            return EMPTY_BYTE_LIST;
        }
        return lines.stream()
                    .filter(this :: target)
                    .map(this :: warpByteArr)
                    .collect(Collectors.toList());
    }

    private boolean target(String line){
        if(StringUtils.isBlank(line)){
            return false;
        }
        return line.indexOf(KEY_STR) > 0;
    }
    private byte[] warpByteArr(String line){
        final char[] chars = line.toCharArray();
        final int offset = line.lastIndexOf(KEY_STR) + KEY_STR.length();
        final String json = new String(chars, offset, chars.length - 1 - offset);
        if(json.length() <= 2){
            return EMPTY_BYTE_ARR;
        }
        final List<Byte> bytes = GsonUtil.GsonToList(json, Byte.class);
        final byte[] result = new byte[bytes.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = bytes.get(i);
        }
        return result;
    }
}