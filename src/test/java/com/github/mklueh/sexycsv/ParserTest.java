package com.github.mklueh.sexycsv;

import com.github.mklueh.sexycsv.annotation.CSVColumn;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by m.kluehspies on 11/03/2021
 */
class ParserTest {

    @Test
    void testParser2() {
        List<MyEntity> entities = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            MyEntity entity = new MyEntity();
            entity.setName("s_" + i);
            entity.setId(String.valueOf(i));
            entity.setAge(i);
            entity.setCountry("bla");
            entities.add(entity);
        }

        SexyCSV.Creator<MyEntity> myEntityParser = SexyCSV.Creator.<MyEntity>builder()
                .withEntity(MyEntity.class)
                .build();

        String text = myEntityParser.toString(entities);
        System.out.println(text);
        assertNotNull(text);
    }
}