/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.usergrid.java.client;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The Client class for accessing the Usergrid API. Start by instantiating this
 * class though the appropriate constructor.
 */
public class UsergridEnums {
    public enum UsergridAuthMode {
        NONE,
        USER,
        APP
    }

    /**
     * An enumeration for defining the HTTP methods used by Usergrid.
     */
    public enum UsergridHttpMethod {
        GET {
            @Override
            public String toString() {
                return "GET";
            }
        },
        POST {
            @Override
            public String toString() {
                return "POST";
            }
        },
        PUT {
            @Override
            public String toString() {
                return "PUT";
            }
        },
        DELETE {
            @Override
            public String toString() {
                return "DELETE";
            }
        }
    }
    public enum UsergridQueryOperator {
        EQUAL("="),
        GREATER_THAN(">"),
        GREATER_THAN_EQUAL_TO(">="),
        LESS_THAN("<"),
        LESS_THAN_EQUAL_TO("<=");

        @Nonnull private final String operatorValue;

        private UsergridQueryOperator(@Nonnull final String operatorValue) {
            this.operatorValue = operatorValue;
        }

        @Nonnull
        public String operatorValue() {
            return this.operatorValue;
        }
    }

    public enum UsergridQuerySortOrder {
        ASC,
        DESC;

        @Nullable
        public static UsergridQuerySortOrder fromString(@Nonnull final String stringValue) {
            try {
                return UsergridQuerySortOrder.valueOf(stringValue.toUpperCase());
            } catch(Exception e) {
                return null;
            }
        }

        @Override @Nonnull
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

}
