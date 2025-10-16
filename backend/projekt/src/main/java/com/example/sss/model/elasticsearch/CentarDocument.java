package com.example.sss.model.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.data.elasticsearch.annotations.Mapping;

import java.util.Date;

@Document(indexName = "centri")
@Setting(settingPath = "elasticsearch-settings.json")
@Mapping(mappingPath = "elasticsearch-mappings.json")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CentarDocument {
    
    @Id
    private Integer id;

    // Multi-field will be defined in elasticsearch-mappings.json
    @Field(type = FieldType.Text)
    private String ime;

    // Multi-field will be defined in elasticsearch-mappings.json
    @Field(type = FieldType.Text)
    private String opis;

    @Field(type = FieldType.Date)
    private Date datumKreacije;

    @Field(type = FieldType.Text)
    private String adresa;

    @Field(type = FieldType.Text)
    private String grad;

    @Field(type = FieldType.Double)
    private Double rating;

    @Field(type = FieldType.Boolean)
    private boolean active;

    @Field(type = FieldType.Text)
    private String imagePath;

    @Field(type = FieldType.Text)
    private String pdfPath;

    // Multi-field will be defined in elasticsearch-mappings.json
    @Field(type = FieldType.Text)
    private String pdfContent;
}
