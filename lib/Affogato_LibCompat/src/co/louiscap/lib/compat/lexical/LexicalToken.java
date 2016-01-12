/*
 Copyright (c) 2015, Louis Capitanchik
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 * Neither the name of Affogato nor the names of its associated properties or
 contributors may be used to endorse or promote products derived from
 this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package co.louiscap.lib.compat.lexical;

import co.louiscap.lib.compat.NamedValue;
import co.louiscap.lib.compat.string.StringChunker;

/**
 * A token that represents an atomic lexical unit. Also holds information about the lexical unit.
 * @author Louis Capitanchik &lt;contact@louiscap.co&gt;
 * @param <T> The data type that this lexical token holds. Provided mostly as a convenience; only 
 * affects get/set methods for the user value
 */
public class LexicalToken<T> implements NamedValue<T> {

    /**
     * The default namespace given to tokens that are defined without one.
     */
    public static final String DEFAULT_NAMESPACE = "default"; 
    
    /**
     * The separator used when serialising data in tokens
     */
    private static final String SERIAL_SEP = "#";

    private enum LexDataTypes {
        dtNull,
        dtLong,
        dtDouble,
        dtString
    }
    
    private String name, namespace;
    private T value;
    
    /**
     * Create a new Token in the default namespace
     * @param name The identifying name of this token
     */
    public LexicalToken (String name) {
        this(name, DEFAULT_NAMESPACE, null);
    }
    
    /**
     * Create a new Token within the specified namespace. Two tokens with the same name (but
     * which exist in different namespaces) can represent two entirely separate entities without
     * conflict
     * @param name The Identifying name of this token
     * @param namespace The namespace within which this token exists
     */
    public LexicalToken (String name, String namespace) {
        this(name, namespace, null);
    }
    
    /**
     * Create a new Token within the specified namespace, and with the given data. Two tokens with 
     * the same name (but which exist in different namespaces) can represent two entirely separate 
     * entities without conflict
     * @param name The Identifying name of this token
     * @param namespace The namespace within which this token exists
     * @param value  A user value that this token represents
     */
    public LexicalToken(String name, String namespace, T value) {
        this.name = name;
        this.namespace = namespace;
        this.value = value;
    }
    
    /**
     * Creates a reversible String representation of the specified token. Values that are not a boxed
     * integer, float, long or double will be marked as strings. They will need to be deserialised 
     * separately after the full token has been decoded from a string.
     * @param val The stringValue to turn into a String
     * @return A String that represents the stringValue and stringValue type of the parameter provided.
     */
    protected String valueToString(T val) {
        StringBuilder result = new StringBuilder();
        String stringVal, typeOfStringVal;
        if(val == null) {
            stringVal = "";
            typeOfStringVal = LexDataTypes.dtNull.toString();
        } else if(val instanceof Long || val instanceof Integer) {
            stringVal = ((Long)val).toString();
            typeOfStringVal = LexDataTypes.dtLong.toString();
        } else if(val instanceof Double || val instanceof Float) {
            stringVal = ((Double)val).toString();
            typeOfStringVal = LexDataTypes.dtDouble.toString();
        } else {
            stringVal = val.toString();
            typeOfStringVal = LexDataTypes.dtString.toString();
        }
        
        result.append(typeOfStringVal);
        result.append(SERIAL_SEP);
        result.append(stringVal);
        
        return result.toString();
    };
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getNamesapce() {
        return namespace;
    }
    
    @Override
    public T getValue() {
        return this.value;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }
    
    @Override
    public String serialise() {
        StringBuilder serialData = new StringBuilder("LexTok");
        
        serialData.append(SERIAL_SEP);
        serialData.append(name);
        
        serialData.append(SERIAL_SEP);
        serialData.append(namespace);
        
        serialData.append(SERIAL_SEP);
        serialData.append(valueToString(this.value));
        
        serialData.append("\n");
        return serialData.toString();
    }
    
    @Override
    public String toString() {
        return serialise();
    }
    
    @Override
    public void deserialise(String data) {
        LexicalToken<T> lt = LexicalToken.tokenFromString(data);
        this.name = lt.name;
        this.namespace = lt.namespace;
        this.value = lt.value;
    }

    @Override
    @Deprecated
    public NamedValue fromString(String data) {
        return LexicalToken.tokenFromString(data);
    }
    
    /**
     * Turns a string into a {@link LexicalToken} as per {@link fromString(java.lang.String)} but 
     * explicitely returns a LexicalToken instead of a {@link NamedValue}
     * @param data A string that represents a LexicalToken, likely created by an invocation of
     * {@link serialise()}.
     * @throws IllegalArgumentException Thrown if the provided data doesn't have the correct token
     * header
     * @return A LexicalToken that represents the provided data
     */
    public static LexicalToken tokenFromString(String data) 
            throws IllegalArgumentException {
        StringChunker stream = new StringChunker(data);
        String type = stream.getUntil(SERIAL_SEP);
        
        if(!type.equals("LexTok")) {
            throw new IllegalArgumentException("Provided token not of type LexicalToken");
        }
        
        String tokenName = stream.getUntil(SERIAL_SEP),
                tokenNamespace = stream.getUntil(SERIAL_SEP),
                valueTypeString = stream.getUntil(SERIAL_SEP),
                stringValue = stream.tail();
        
        LexDataTypes valueType = LexDataTypes.valueOf(valueTypeString);
        LexicalToken result = new LexicalToken(tokenName, tokenNamespace);
        
        switch(valueType){
            case dtLong:
                Long longVal = Long.valueOf(stringValue);
                result.setValue(longVal);
                break;
            case dtDouble:
                Double doubleVal = Double.valueOf(stringValue);
                result.setValue(doubleVal);
                break;
            case dtString:
                result.setValue(stringValue);
            case dtNull:
            default:
                break;
        }
        
        return result;
    }
    
    
}
