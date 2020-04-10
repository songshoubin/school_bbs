package com.example.entity;

import lombok.Data;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;


@Data
public class BaseEntity {

	@JsonSerialize(using=ToStringSerializer.class)  
    private Long id;
    private Date created;
    private Date modified;

}
