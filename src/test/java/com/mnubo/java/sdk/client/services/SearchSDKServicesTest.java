package com.mnubo.java.sdk.client.services;

import com.mnubo.java.sdk.client.config.MnuboSDKConfig;
import com.mnubo.java.sdk.client.models.DataSet;
import com.mnubo.java.sdk.client.models.Field;
import com.mnubo.java.sdk.client.models.result.*;
import com.mnubo.java.sdk.client.models.result.ResultSet.ColumnDefinition;
import com.mnubo.java.sdk.client.models.result.SearchResult.Column;
import com.mnubo.java.sdk.client.spi.SearchSDK;
import com.mnubo.java.sdk.client.utils.Convert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.mnubo.java.sdk.client.services.SearchSDKServices.BASIC_SEARCH_PATH;
import static com.mnubo.java.sdk.client.services.SearchSDKServices.DATASETS_SEARCH_PATH;
import static java.lang.String.format;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SearchSDKServicesTest {
    private static String HOST = "host";
    private static RestTemplate restTemplate = mock(RestTemplate.class);
    private static ClientSecretCredentialHandler credentials = mock(ClientSecretCredentialHandler.class);
    private static MnuboSDKConfig config = MnuboSDKConfig.builder().withHostName(HOST).withSecurityConsumerKey("CK")
            .withSecurityConsumerSecret("SC").build();
    private static MnuboSDKClientImpl client = new MnuboSDKClientImpl(config, restTemplate, credentials);

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private static SearchSDK searchClient;

    // Query example (not used due to mock of RestTemplate)
    private static final String query = "{ \"from\": \"event\", \"select\": [ {\"value\": \"speed\"} ] }";

    private static ResponseEntity httpResponse = mock(ResponseEntity.class);

    private static ResultSet expectedResultSet;
    private static List<DataSet> datasets;
    private static SearchResult searchResult;

    @BeforeClass
    public static void searchSDKSetup() {
        searchClient = client.getSearchClient();

        // Create Field Data
        Set<Field> fields = new HashSet<Field>();
        fields.add(new Field("F1","TEXT","Field1","Field test 1","list"));
        fields.add(new Field("F2","BOOLEAN","Field2","Field test 2","list"));

        // Create data for DataSets with the Field data
        datasets = new ArrayList<>();
        datasets.add(new DataSet("KeyData1", "DataSet Test 1", "DataSet1", fields));
        datasets.add(new DataSet("KeyData2", "DataSet Test 2", "DataSet2", fields));

        // Create data for the Column Definition
        final List<ColumnDefinition> columnDefinitions = new ArrayList<>();
        columnDefinitions.add(new ColumnDefinition("column1", "TEXT"));
        columnDefinitions.add(new ColumnDefinition("column2", "BOOLEAN"));
        columnDefinitions.add(new ColumnDefinition("columnTest3", "DOUBLE"));

        List<Column> columns = new ArrayList<>();
        columns.add(new Column("column1", "TEXT"));
        columns.add(new Column("column2", "BOOLEAN"));
        columns.add(new Column("columnTest3", "DOUBLE"));

        // Create data for Search Result
        final List<List<Object>> searchResultRows = new ArrayList<>();
        searchResultRows.add(searchResultRow("test String 1", true, 24.66));
        searchResultRows.add(searchResultRow("test String 2", true, 0.25));
        searchResultRows.add(searchResultRow("test String 3", false, 100.99));
        searchResultRows.add(searchResultRow("test String 4", true, 44.00));
        searchResultRows.add(searchResultRow("test String 5", false, 66.777));
        searchResult = new SearchResult(columns, searchResultRows);

        // Create data for Search Result Set
        List<Row> rows = new ArrayList<Row>() {{
            add(buildRows("test String 1", true, 24.66, columnDefinitions));
            add(buildRows("test String 2", true, 0.25, columnDefinitions));
            add(buildRows("test String 3", false, 100.99, columnDefinitions));
            add(buildRows("test String 4", true, 44.00, columnDefinitions));
            add(buildRows("test String 5", false, 66.777, columnDefinitions));
        }};


        expectedResultSet = new SearchResultSet(columnDefinitions, rows);
    }


    @Test
    public void searchBasicThenOk() {

        // Mock Call to POST Search
        when(restTemplate.postForObject(any(String.class), any(Object.class), eq(SearchResult.class)))
                .thenReturn(searchResult);

        String url = client.getSdkService().getRestitutionBaseUri().path(BASIC_SEARCH_PATH).build().toString();
        assertThat(url, is(equalTo(format("https://%s:443/api/v3/search/basic", HOST))));
        ResultSet testSearchResultSet = searchClient.search(query);

        assertThat(testSearchResultSet.getColumnDefinitions(), equalTo(expectedResultSet.getColumnDefinitions()));
        assertThat(testSearchResultSet.all(), equalTo(expectedResultSet.all()));
    }

    @Test
    public void searchBasicNullThenFail() {

        // Mock Call to POST Search
        when(restTemplate.postForObject(any(String.class), any(Object.class), eq(SearchResult.class)))
                .thenReturn(searchResult);

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Query body cannot be null.");
        ResultSet searchResultSet = searchClient.search(null);
    }

    @Test
    public void searchBasicReturnNullThenReturnNull() {

        // Mock Call to POST Search
        when(restTemplate.postForObject(any(String.class), any(Object.class), eq(SearchResult.class))).thenReturn(null);

        String url = client.getSdkService().getRestitutionBaseUri().path(BASIC_SEARCH_PATH).build().toString();
        assertThat(url, is(equalTo(format("https://%s:443/api/v3/search/basic", HOST))));
        ResultSet testSearchResultSet = searchClient.search(query);

        assertThat(testSearchResultSet, is(equalTo(null)));
    }

    @Test
    public void searchBasicWithIteratorThenOk() {

        // Mock Call to POST Search
        when(restTemplate.postForObject(any(String.class), any(Object.class), eq(SearchResult.class)))
                .thenReturn(searchResult);

        String url = client.getSdkService().getRestitutionBaseUri().path(BASIC_SEARCH_PATH).build().toString();
        assertThat(url, is(equalTo(format("https://%s:443/api/v3/search/basic", HOST))));
        ResultSet testSearchResultSet = searchClient.search(query);

        while (testSearchResultSet.iterator().hasNext()) {
            Row searchRow = testSearchResultSet.iterator().next();

            // Verify Column Definitions for each Search Row
            List<ColumnDefinition> columnDefinitions = searchRow.getColumnDefinitions();
            for (ColumnDefinition columnDefinition : columnDefinitions) {
                assertThat(columnDefinition.getLabel(), anyOf(is("column1"), is("column2"), is("columnTest3")));
                assertThat(columnDefinition.getHighLevelType(), anyOf(is("TEXT"), is("BOOLEAN"), is("DOUBLE")));
                assertThat(columnDefinition.getPrimitiveType(), anyOf(is("STRING"), is("BOOLEAN"), is("DOUBLE")));
            }

            // Verify Data For Each Row
            assertThat(searchRow.getString("column1"), anyOf(is("test String 1"), is("test String 2"),
                    is("test String 3"), is("test String 4"), is("test String 5")));
            assertThat(searchRow.getBoolean("column2"), anyOf(is(true), is(false)));
            assertThat(searchRow.getDouble("columnTest3"),
                    anyOf(is(24.66), is(0.25), is(100.99), is(44.00), is(66.777)));
        }
    }

    @Test
    public void searchDatasetsThenOk() {

        // Mock Call to GET DataSets
        when(httpResponse.getBody()).thenReturn(datasets);
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(List.class)))
                .thenReturn(httpResponse);

        final String url = client.getSdkService().getRestitutionBaseUri().path(DATASETS_SEARCH_PATH)
                                      .build().toString();

        assertThat(url, is(equalTo(format("https://%s:443/api/v3/search/datasets", HOST))));

        List<DataSet> testDatasets = searchClient.getDatasets();

        assertThat(testDatasets, is(equalTo(datasets)));
    }
    
    @Test
    public void searchDatasetsEmptyThenReturnNull() {

        // Mock Call to GET DataSets
        when(httpResponse.getBody()).thenReturn(null);
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(List.class)))
                .thenReturn(httpResponse);

        final String url = client.getSdkService().getRestitutionBaseUri().path(DATASETS_SEARCH_PATH)
                                      .build().toString();

        assertThat(url, is(equalTo(format("https://%s:443/api/v3/search/datasets", HOST))));

        List<DataSet> testDatasets = searchClient.getDatasets();

        assertThat(testDatasets, is(equalTo(null)));
    }

    private static SearchRow buildRows(Object value1, Object value2, Object value3,
            List<ColumnDefinition> definitions ) {
        List<ResultValue> rows = new ArrayList<>();
        Convert.toResultValue(value1);
        rows.add(Convert.toResultValue(value1));
        rows.add(Convert.toResultValue(value2));
        rows.add(Convert.toResultValue(value3));
        return new SearchRow(definitions, rows);
    }

    public static List<Object> searchResultRow(Object value1, Object value2, Object value3) {

        List<Object> row = new ArrayList<>();
        row.add(value1);
        row.add(value2);
        row.add(value3);
        return row;
    }
}
