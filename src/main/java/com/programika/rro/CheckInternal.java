package com.programika.rro;


import lombok.Builder;
import lombok.Data;


import java.util.Date;


@Data
@Builder
public class CheckInternal {
    private String fn;
    private Date date;
    private byte[] data;
    private Integer number;
    private Integer type;
    private String idCancel;
    private String idOffline;
}
