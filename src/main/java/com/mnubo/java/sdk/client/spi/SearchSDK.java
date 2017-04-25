package com.mnubo.java.sdk.client.spi;

import java.util.List;

import com.mnubo.java.sdk.client.models.DataSet;
import com.mnubo.java.sdk.client.models.result.ResultSet;

/**
 * Search SDK Client. This interface gives access to Search APIs.
 */
public interface SearchSDK {

    /**
     * Use the Search API to perform a wide range of analytics on your data.
     * @param query, the query in mnubo query language (MQL)
     * 
     * @return the result set of the query sent with all rows and columns definition.
     */
    ResultSet search(String query);

    /**
     * @return the list of datasets that can be searched.
     * This list is in JSON format and describes what can be queried 
     * for objects, events, and owners. 
     */
    List<DataSet> getDatasets();

}