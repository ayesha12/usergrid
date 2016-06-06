/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.usergrid.persistence.graph.impl;


import org.apache.usergrid.persistence.graph.Edge;
import org.apache.usergrid.persistence.graph.MarkedEdge;
import org.apache.usergrid.persistence.model.entity.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * Simple bean to represent our edge
 *
 * @author tnine
 */
public class SimpleMarkedEdge extends SimpleEdge implements MarkedEdge {

    private boolean isDeleted;
    private boolean isSourceNodeDeleted;
    private boolean isTargetNodeDeleted;
    private long edge_expires_in;


    /**
     * Unused but required for Jackson
     */
    public SimpleMarkedEdge() {
    }


    public SimpleMarkedEdge( final Id sourceNode, final String type, final Id targetNode, final long timestamp,
                             final boolean isDeleted, final long edge_expires_in ) {

        this( sourceNode, type, targetNode, timestamp, isDeleted, false, false, edge_expires_in);
    }


    public SimpleMarkedEdge( final Id sourceNode, final String type, final Id targetNode, final long timestamp,
                             final boolean isDeleted, final boolean isSourceNodeDeleted,
                             final boolean isTargetNodeDeleted, final long edge_expires_in ) {
        super( sourceNode, type, targetNode, timestamp, edge_expires_in ); //todo : check the edge_expiration.
        this.isDeleted = isDeleted;
        this.isSourceNodeDeleted = isSourceNodeDeleted;
        this.isTargetNodeDeleted = isTargetNodeDeleted;
        this.edge_expires_in = edge_expires_in;
    }


    public SimpleMarkedEdge( final Edge edge, final boolean isDeleted ) {
        this( edge.getSourceNode(), edge.getType(), edge.getTargetNode(), edge.getTimestamp(), isDeleted, edge.getEdgeExpiration() );
    }


    @Override
    @JsonIgnore
    public boolean isDeleted() {
        return isDeleted;
    }


    @Override
    @JsonIgnore
    public boolean isSourceNodeDelete() {
        return isSourceNodeDeleted;
    }


    @Override
    @JsonIgnore
    public boolean isTargetNodeDeleted() {
        return isTargetNodeDeleted;
    }

    @Override
    @JsonIgnore
    public long edgeExpiresIn(){
        return edge_expires_in;
    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof SimpleMarkedEdge ) ) {
            return false;
        }
        if ( !super.equals( o ) ) {
            return false;
        }

        final SimpleMarkedEdge that = ( SimpleMarkedEdge ) o;

        if ( isDeleted != that.isDeleted ) {
            return false;
        }

        return true;
    }


    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + ( isDeleted ? 1 : 0 );
        result = 31 * result + ( isSourceNodeDeleted ? 1 : 0 );
        result = 31 * result + ( isTargetNodeDeleted ? 1 : 0 );
        //// TODO: 4/25/16 : should we add edge_expires_in?
        return result;
    }


    @Override
    public String toString() {
        return "SimpleMarkedEdge{" +
            "deleted=" + isDeleted +
            ", isSourceNodeDeleted=" + isSourceNodeDeleted +
            ", isTargetNodeDeleted=" + isTargetNodeDeleted +
            ", edgeExpiresIn=" + edge_expires_in +
            "} " + super.toString();
    }
}
