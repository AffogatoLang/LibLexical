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
package co.louiscap.lib.compat;

/**
 * A data structure for associating a value with a name and optional namespace
 * @author Louis Capitanchik &lt;contact@louiscap.co&gt;
 * @param <T> The type of value being held
 */
public interface NamedValue<T> {
    
    /**
     * Gets the name of the value.
     * @return The name of the value
     */
    public String getName();
    
    /**
     * Gets the namespace that the value exists within. This allows multiple leaves with the same 
     * name to represent different items
     * @return The namespace for this value
     */
    public String getNamesapce();
    
    /**
     * Gets the value that this object represents
     * @return The value that this object represents
     */
    public T getValue();
    
    /**
     * Sets the value represented by this object
     * @param val The new value to store in this object
     */
    public void setValue(T val);
    
    /**
     * Create a string that represents this object, in a format that can be reversed with either 
     * {@link deserialise(java.lang.String)} or {@link fromString(java.lang.String)}
     * @return A string that represents the current state of the NamedValue
     */
    public String serialise();
    
    /**
     * Create a string that represents this object, in a format that can be reversed with either 
     * {@link deserialise(java.lang.String)} or {@link fromString(java.lang.String)}
     * @return A string that represents the current state of the NamedValue
     */
    @Override
    public String toString();
    
    /**
     * Take a string that represents a NamedValue (presumably created by {@link serialise()} and set 
     * this NamedValue instance to be equal to it.
     * @param data A String that represents a NamedValue of the same type as the implementation
     */
    public void deserialise(String data);
    
    /**
     * Take a string that represents a NamedValue (presumably created by {@link serialise()} and 
     * creates a new NamedValue instance (of the same type as the implementation) from it
     * @param data A String that represents a NamedValue of the same type as the implementation
     * @return A new NamedValue that mirrors the provided string data
     */
    public NamedValue fromString(String data);
}
