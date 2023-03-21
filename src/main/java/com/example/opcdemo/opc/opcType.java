package com.example.opcdemo.opc;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

public enum opcType {
    Boolean(1),
    SByte(2),
    Byte(3),
    Int16(4),
    UInt16(5),
    Int32(6),
    UInt32(7),
    Int64(8),
    UInt64(9),
    Float(10),
    Double(11),
    String(12),
    DateTime(13),
    Guid(14),
    ByteString(15),
    XmlElement(16);
    
    @JsonValue
    @Getter
    private final int value;

    opcType(int value) {
        this.value = value;
    }
    
}
