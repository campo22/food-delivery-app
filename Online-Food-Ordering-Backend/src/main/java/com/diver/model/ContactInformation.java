package com.diver.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
public class ContactInformation {

    private String email;
    private String mobilePhone;
    private String  twitter;
    private String instagram ;

}