package com.zoi.drive;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.sql.Types;

public class CodeGenerator {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:postgresql://192.168.6.156:5432/drive", "postgres", "123456")
                .globalConfig(builder -> {
                    builder.author("Yuzoi") // 设置作者
                            .enableSpringdoc() // 开启 swagger 模式
                            .disableOpenDir()
                            .outputDir("src/main/java"); // 指定输出目录
                })
                .dataSourceConfig(builder ->
                        builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                            int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                            if (typeCode == Types.SMALLINT) {
                                // 自定义类型转换
                                return DbColumnType.INTEGER;
                            }
                            return typeRegistry.getColumnType(metaInfo);
                        })
                )
                .packageConfig(builder ->
                        builder.parent("com.zoi") // 设置父包名
                                .moduleName("drive")
                                .entity("entity.dto")
                                .mapper("mapper")
                                .service("service")
                                .serviceImpl("service.impl")
                )
                .strategyConfig(builder ->
                        builder
                                .addTablePrefix("db_")
                                .entityBuilder().enableLombok()
                                .javaTemplate("/templates/entity.java")
                                .serviceBuilder().serviceTemplate("/templates/service.java")
                                .serviceBuilder().serviceImplTemplate("/templates/serviceimpl.java")
                                .mapperBuilder().mapperTemplate("/templates/mapper.java")
                                .disableMapperXml()
                                .controllerBuilder().disable()
                                .build()
                )
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}
