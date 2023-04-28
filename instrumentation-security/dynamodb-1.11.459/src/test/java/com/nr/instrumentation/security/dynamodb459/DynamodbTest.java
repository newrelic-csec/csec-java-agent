package com.nr.instrumentation.security.dynamodb459;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.KeysAndAttributes;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutRequest;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.newrelic.agent.security.introspec.InstrumentationTestConfig;
import com.newrelic.agent.security.introspec.SecurityInstrumentationTestRunner;
import com.newrelic.agent.security.introspec.SecurityIntrospector;
import com.newrelic.api.agent.security.schema.AbstractOperation;
import com.newrelic.api.agent.security.schema.VulnerabilityCaseType;
import com.newrelic.api.agent.security.schema.helper.DynamoDBRequest;
import com.newrelic.api.agent.security.schema.operation.DynamoDBOperation;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.List;
import java.util.Map;

@RunWith(SecurityInstrumentationTestRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@InstrumentationTestConfig(includePrefixes = {"com.nr.agent.security.dynamodb_1_11_459", "com.amazonaws.services.dynamodbv2_1_11_459"})
public class DynamodbTest {
    @ClassRule
    public static DynamoServer dynamo = new DynamoServer();
    @Test
    public void testBatchWriteItem() {
        dynamo.batchWriteTxn();

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("No operations detected", operations.size() > 0);

        DynamoDBOperation operation = (DynamoDBOperation) operations.get(0);
        Assert.assertEquals("Invalid event category.", VulnerabilityCaseType.DYNAMO_DB_COMMAND, operation.getCaseType());
        Assert.assertEquals("Invalid executed method name.", "executeBatchWriteItem", operation.getMethodName());
        Assert.assertEquals("Invalid operation Category.", DynamoDBOperation.Category.DQL, operation.getCategory());
        Assert.assertTrue("No payload detected", operation.getPayload().size() > 0);

        for(DynamoDBRequest request: operation.getPayload()) {
            PutRequest query = (PutRequest)request.getQuery();
            Assert.assertNotNull("No such payload detected", query.getItem().get("artist"));
            Assert.assertEquals("Invalid payload value.", "Charlie",query.getItem().get("artist").getS());
            Assert.assertNotNull("No such payload detected", query.getItem().get("year"));
            Assert.assertEquals("Invalid payload value.", "2000", query.getItem().get("year").getN());
            Assert.assertNotNull("No such payload detected", query.getItem().get("Genre"));
            Assert.assertEquals("Invalid payload value.", "Jazz", query.getItem().get("Genre").getS());
            Assert.assertEquals("Invalid query-type.", "write", request.getQueryType());
        }
    }
    @Test
    public void testBatchWriteItem1() {
        dynamo.batchWriteTxn1();

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("No operations detected", operations.size() > 0);

        DynamoDBOperation operation = (DynamoDBOperation) operations.get(0);
        Assert.assertEquals("Invalid event category.", VulnerabilityCaseType.DYNAMO_DB_COMMAND, operation.getCaseType());
        Assert.assertEquals("Invalid executed method name.", "executeBatchWriteItem", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            PutRequest query = (PutRequest)request.getQuery();
            Assert.assertNotNull("No such payload detected", query.getItem().get("artist"));
            Assert.assertEquals("Invalid payload value.", "Charlie",query.getItem().get("artist").getS());
            Assert.assertNotNull("No such payload detected", query.getItem().get("year"));
            Assert.assertEquals("Invalid payload value.", "2000", query.getItem().get("year").getN());
            Assert.assertNotNull("No such payload detected", query.getItem().get("Genre"));
            Assert.assertEquals("Invalid payload value.", "Jazz", query.getItem().get("Genre").getS());
            Assert.assertEquals("Invalid query-type.", "write", request.getQueryType());
        }
    }
    @Test
    public void testBatchGetItem0() {
        dynamo.batchGetTxn0();

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertEquals("operations should not be detected", 0, operations.size());
    }
    @Test
    public void testBatchGetItem() {
        dynamo.batchGetTxn();

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("No operations detected", operations.size() > 0);

        DynamoDBOperation operation = (DynamoDBOperation) operations.get(0);
        Assert.assertEquals("Invalid event category.", VulnerabilityCaseType.DYNAMO_DB_COMMAND, operation.getCaseType());
        Assert.assertEquals("Invalid executed method name.", "executeBatchGetItem", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            KeysAndAttributes attributes = (KeysAndAttributes) request.getQuery();
            List<Map<String, AttributeValue>> keys = attributes.getKeys();

            Assert.assertTrue("No keys detected", keys.size() > 0);
            Assert.assertNotNull("No such payload detected", keys.get(0).get("artist"));
            Assert.assertEquals("Invalid payload value.", "Charlie", keys.get(0).get("artist").getS());
            Assert.assertNotNull("No such payload detected", keys.get(0).get("year"));
            Assert.assertEquals("Invalid payload value.", "2000", keys.get(0).get("year").getN());

            Assert.assertEquals("Invalid projection expression.", "artist, Genre", attributes.getProjectionExpression());
            Assert.assertFalse("read consistency should be false", attributes.getConsistentRead());
            Assert.assertEquals("Invalid query-type.", "read", request.getQueryType());
        }
    }
    @Test
    public void testBatchGetItem1() {
        dynamo.batchGetTxn1();

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("No operations detected", operations.size() > 0);

        DynamoDBOperation operation = (DynamoDBOperation) operations.get(0);
        Assert.assertEquals("Invalid event category.", VulnerabilityCaseType.DYNAMO_DB_COMMAND, operation.getCaseType());
        Assert.assertEquals("Invalid executed method name.", "executeBatchGetItem", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            KeysAndAttributes attributes = (KeysAndAttributes) request.getQuery();
            List<Map<String, AttributeValue>> keys = attributes.getKeys();

            Assert.assertTrue("No keys detected", keys.size() > 0);
            Assert.assertNotNull("No such payload detected", keys.get(0).get("artist"));
            Assert.assertEquals("Invalid payload value.", "Charlie", keys.get(0).get("artist").getS());
            Assert.assertNotNull("No such payload detected", keys.get(0).get("year"));
            Assert.assertEquals("Invalid payload value.", "2000", keys.get(0).get("year").getN());

            Assert.assertEquals("Invalid projection expression.", "artist, Genre", attributes.getProjectionExpression());
            Assert.assertFalse("read consistency should be false", attributes.getConsistentRead());
            Assert.assertEquals("Invalid query-type.", "read", request.getQueryType());
        }
    }
    @Test
    public void testDeleteItem0() {
        dynamo.deleteItemTxn0();

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("No operations detected", operations.size() > 0);
        for (AbstractOperation op: operations){
            Assert.assertNotEquals("delete op should not be detected", "executeDeleteItem",  op.getMethodName());
        }
    }
    @Test
    public void testDeleteItem() {
        dynamo.deleteItemTxn();

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("No operations detected", operations.size() > 0);

        DynamoDBOperation operation = null;
        for (AbstractOperation op: operations){
            if(op.getMethodName().equals("executeDeleteItem")){
                operation = (DynamoDBOperation) op;
            }
        }
        Assert.assertEquals("Invalid event category.", VulnerabilityCaseType.DYNAMO_DB_COMMAND, operation.getCaseType());
        Assert.assertEquals("Invalid executed method name.", "executeDeleteItem", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            DeleteItemRequest query = (DeleteItemRequest) request.getQuery();
            Assert.assertEquals("No such payload detected", "test", query.getTableName());
            Assert.assertTrue("No keys detected", query.getKey().size() > 0);

            Assert.assertNotNull("No such keys detected", query.getKey().get("artist"));
            Assert.assertEquals("Invalid payload value.", "Charlie",query.getKey().get("artist").getS());
            Assert.assertNotNull("No such keys detected", query.getKey().get("year"));
            Assert.assertEquals("Invalid payload value.", "2000",query.getKey().get("year").getN());

            Assert.assertEquals("Invalid Condition-Expression.", "Genre = :val", query.getConditionExpression());
            Assert.assertNotNull("No Expression-AttributeValues detected.", query.getExpressionAttributeValues());
            Assert.assertNotNull("No such Attribute-value detected.", query.getExpressionAttributeValues().get(":val"));
            Assert.assertEquals("No such Attribute-value detected.", "Jazz", query.getExpressionAttributeValues().get(":val").getS());
            Assert.assertEquals("Invalid query-type.", "delete", request.getQueryType());
        }
    }
    @Test
    public void testDeleteItem1() {
        dynamo.deleteItemTxn1();

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("No operations detected", operations.size() > 0);

        DynamoDBOperation operation = null;
        for (AbstractOperation op: operations){
            if(op.getMethodName().equals("executeDeleteItem")){
                operation = (DynamoDBOperation) op;
            }
        }
        Assert.assertEquals("Invalid event category.", VulnerabilityCaseType.DYNAMO_DB_COMMAND, operation.getCaseType());
        Assert.assertEquals("Invalid executed method name.", "executeDeleteItem", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            DeleteItemRequest query = (DeleteItemRequest) request.getQuery();
            Assert.assertEquals("No such payload detected", "test", query.getTableName());
            Assert.assertTrue("No keys detected", query.getKey().size() > 0);

            Assert.assertNotNull("No such keys detected", query.getKey().get("artist"));
            Assert.assertEquals("Invalid payload value.", "Charlie",query.getKey().get("artist").getS());
            Assert.assertNotNull("No such keys detected", query.getKey().get("year"));
            Assert.assertEquals("Invalid payload value.", "2000",query.getKey().get("year").getN());

            Assert.assertEquals("Invalid Condition-Expression.", "Genre = :val", query.getConditionExpression());
            Assert.assertNotNull("No Expression-AttributeValues detected.", query.getExpressionAttributeValues());
            Assert.assertNotNull("No such Attribute-value detected.", query.getExpressionAttributeValues().get(":val"));
            Assert.assertEquals("No such Attribute-value detected.", "Jazz", query.getExpressionAttributeValues().get(":val").getS());
            Assert.assertEquals("Invalid query-type.", "delete", request.getQueryType());
        }
    }
    @Test
    public void testDeleteItem2() {
        dynamo.deleteItemTxn2();

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("No operations detected", operations.size() > 0);

        DynamoDBOperation operation = null;
        for (AbstractOperation op: operations){
            if(op.getMethodName().equals("executeDeleteItem")){
                operation = (DynamoDBOperation) op;
            }
        }
        Assert.assertEquals("Invalid event category.", VulnerabilityCaseType.DYNAMO_DB_COMMAND, operation.getCaseType());
        Assert.assertEquals("Invalid executed method name.", "executeDeleteItem", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            DeleteItemRequest query = (DeleteItemRequest) request.getQuery();
            Assert.assertEquals("No such payload detected", "test", query.getTableName());
            Assert.assertTrue("No keys detected", query.getKey().size() > 0);

            Assert.assertNotNull("No such keys detected", query.getKey().get("artist"));
            Assert.assertEquals("Invalid payload value.", "Charlie",query.getKey().get("artist").getS());
            Assert.assertNotNull("No such keys detected", query.getKey().get("year"));
            Assert.assertEquals("Invalid payload value.", "2000",query.getKey().get("year").getN());

            Assert.assertEquals("Invalid Condition-Expression.", "attribute_exists(#0)", query.getConditionExpression());
            Assert.assertEquals("Invalid query-type.", "delete", request.getQueryType());
        }
    }
    @Test
    public void testDeleteItem3() {
        dynamo.deleteItemTxn3();

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("No operations detected", operations.size() > 0);

        DynamoDBOperation operation = null;
        for (AbstractOperation op: operations){
            if(op.getMethodName().equals("executeDeleteItem")){
                operation = (DynamoDBOperation) op;
            }
        }
        Assert.assertEquals("Invalid event category.", VulnerabilityCaseType.DYNAMO_DB_COMMAND, operation.getCaseType());
        Assert.assertEquals("Invalid executed method name.", "executeDeleteItem", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            DeleteItemRequest query = (DeleteItemRequest) request.getQuery();
            Assert.assertEquals("No such payload detected", "test", query.getTableName());
            Assert.assertTrue("No keys detected", query.getKey().size() > 0);

            Assert.assertNotNull("No such keys detected", query.getKey().get("artist"));
            Assert.assertEquals("Invalid payload value.", "Charlie",query.getKey().get("artist").getS());
            Assert.assertNotNull("No such keys detected", query.getKey().get("year"));
            Assert.assertEquals("Invalid payload value.", "2000",query.getKey().get("year").getN());

            Assert.assertEquals("Invalid Condition-Expression.", "Genre = :val", query.getConditionExpression());
            Assert.assertNotNull("No Expression-AttributeValues detected.", query.getExpressionAttributeValues());
            Assert.assertNotNull("No such Attribute-value detected.", query.getExpressionAttributeValues().get(":val"));
            Assert.assertEquals("No such Attribute-value detected.", "Jazz", query.getExpressionAttributeValues().get(":val").getS());
            Assert.assertEquals("Invalid query-type.", "delete", request.getQueryType());
        }
    }
    @Test
    public void testDeleteItem4() {
        dynamo.deleteItemTxn4();

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("No operations detected", operations.size() > 0);

        DynamoDBOperation operation = null;
        for (AbstractOperation op: operations){
            if(op.getMethodName().equals("executeDeleteItem")){
                operation = (DynamoDBOperation) op;
            }
        }
        Assert.assertEquals("Invalid event category.", VulnerabilityCaseType.DYNAMO_DB_COMMAND, operation.getCaseType());
        Assert.assertEquals("Invalid executed method name.", "executeDeleteItem", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            DeleteItemRequest query = (DeleteItemRequest) request.getQuery();
            Assert.assertEquals("No such payload detected", "test", query.getTableName());
            Assert.assertTrue("No keys detected", query.getKey().size() > 0);

            Assert.assertNotNull("No such keys detected", query.getKey().get("artist"));
            Assert.assertEquals("Invalid payload value.", "Charlie",query.getKey().get("artist").getS());
            Assert.assertNotNull("No such keys detected", query.getKey().get("year"));
            Assert.assertEquals("Invalid payload value.", "2000",query.getKey().get("year").getN());

            Assert.assertEquals("Invalid Condition-Expression.", "attribute_exists(#0)", query.getConditionExpression());
            Assert.assertEquals("Invalid query-type.", "delete", request.getQueryType());
        }
    }
    @Test
    public void testQueryItems() {
        dynamo.queryItemTxn();

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("No operations detected", operations.size() > 0);

        DynamoDBOperation operation = (DynamoDBOperation) operations.get(0);
        Assert.assertEquals("Invalid event category.", VulnerabilityCaseType.DYNAMO_DB_COMMAND, operation.getCaseType());
        Assert.assertEquals("Invalid executed method name.", "executeQuery", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            QueryRequest query = (QueryRequest) request.getQuery();
            Assert.assertEquals("No such payload detected", "test", query.getTableName());
            Assert.assertTrue("read consistency should be true", query.getConsistentRead());

            Assert.assertEquals("Invalid Condition-Expression.", "artist = :val", query.getKeyConditionExpression());
            Assert.assertNotNull("No Expression-AttributeValues detected.", query.getExpressionAttributeValues());
            Assert.assertNotNull("No such Attribute-value detected.", query.getExpressionAttributeValues().get(":val"));
            Assert.assertEquals("No such Attribute-value detected.", "Charlie", query.getExpressionAttributeValues().get(":val").getS());
            Assert.assertEquals("Invalid query-type.", "read", request.getQueryType());
        }
    }
    @Test
    public void testQueryItems1() {
        dynamo.queryItemTxn1();

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("No operations detected", operations.size() > 0);

        DynamoDBOperation operation = (DynamoDBOperation) operations.get(0);Assert.assertEquals("Invalid event category.", VulnerabilityCaseType.DYNAMO_DB_COMMAND, operation.getCaseType());
        Assert.assertEquals("Invalid executed method name.", "executeQuery", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            QueryRequest query = (QueryRequest) request.getQuery();
            Assert.assertEquals("No such payload detected", "test", query.getTableName());
            Assert.assertTrue("read consistency should be true", query.getConsistentRead());

            Assert.assertEquals("Invalid Condition-Expression.", "artist = :val", query.getKeyConditionExpression());
            Assert.assertNotNull("No Expression-AttributeValues detected.", query.getExpressionAttributeValues());
            Assert.assertNotNull("No such Attribute-value detected.", query.getExpressionAttributeValues().get(":val"));
            Assert.assertEquals("No such Attribute-value detected.", "Charlie", query.getExpressionAttributeValues().get(":val").getS());
            Assert.assertEquals("Invalid query-type.", "read", request.getQueryType());
        }
    }
    @Test
    public void testQueryItems2() {
        dynamo. queryItemTxn2();

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("No operations detected", operations.size() > 0);

        DynamoDBOperation operation = (DynamoDBOperation) operations.get(0);
        Assert.assertEquals("Invalid executed method name.", "executeQuery", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            QueryRequest query = (QueryRequest) request.getQuery();
            Assert.assertEquals("No such payload detected", "test", query.getTableName());
            Assert.assertTrue("read consistency should be true", query.getConsistentRead());

            Assert.assertEquals("Invalid Condition-Expression.", "artist = :val", query.getKeyConditionExpression());
            Assert.assertNotNull("No Expression-AttributeValues detected.", query.getExpressionAttributeValues());
            Assert.assertNotNull("No such Attribute-value detected.", query.getExpressionAttributeValues().get(":val"));
            Assert.assertEquals("No such Attribute-value detected.", "Charlie", query.getExpressionAttributeValues().get(":val").getS());
            Assert.assertEquals("Invalid query-type.", "read", request.getQueryType());
        }
    }
    @Test
    public void testGetItems0() {
        dynamo.getItemTxn0();

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertEquals("operations should not be detected", 0, operations.size());
    }
    @Test
    public void testGetItems() {
        dynamo.getItemTxn();

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("No operations detected", operations.size() > 0);

        DynamoDBOperation operation = (DynamoDBOperation) operations.get(0);
        Assert.assertEquals("Invalid executed method name.", "executeGetItem", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            GetItemRequest query = (GetItemRequest) request.getQuery();
            Assert.assertEquals("No such payload detected", "test", query.getTableName());
            Assert.assertTrue("read consistency should be true", query.getConsistentRead());

            Map<String, AttributeValue> keys = query.getKey();
            Assert.assertTrue("No keys detected", keys.size() > 0);
            Assert.assertNotNull("No such payload detected", keys.get("artist"));
            Assert.assertEquals("Invalid payload value.", "Charlie", keys.get("artist").getS());
            Assert.assertNotNull("No such payload detected", keys.get("year"));
            Assert.assertEquals("Invalid payload value.", "2000", keys.get("year").getN());

            Assert.assertEquals("Invalid projection expression.", "artist, Genre", query.getProjectionExpression());
            Assert.assertEquals("Invalid query-type.", "read", request.getQueryType());
        }
    }
    @Test
    public void testGetItems1() {
        dynamo.getItemTxn1();

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("No operations detected", operations.size() > 0);

        DynamoDBOperation operation = (DynamoDBOperation) operations.get(0);
        Assert.assertEquals("Invalid executed method name.", "executeGetItem", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            GetItemRequest query = (GetItemRequest) request.getQuery();
            Assert.assertEquals("No such payload detected", "test", query.getTableName());

            Map<String, AttributeValue> keys = query.getKey();
            Assert.assertTrue("No keys detected", keys.size() > 0);
            Assert.assertNotNull("No such payload detected", keys.get("artist"));
            Assert.assertEquals("Invalid payload value.", "Charlie", keys.get("artist").getS());
            Assert.assertNotNull("No such payload detected", keys.get("year"));
            Assert.assertEquals("Invalid payload value.", "2000", keys.get("year").getN());

            Assert.assertEquals("Invalid projection expression.", "#0", query.getProjectionExpression());
            Assert.assertEquals("Invalid projection expression.", "artist, Genre", query.getExpressionAttributeNames().get("#0"));
            Assert.assertEquals("Invalid query-type.", "read", request.getQueryType());
        }
    }
    @Test
    public void testGetItems2() {
        dynamo.getItemTxn2();

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("No operations detected", operations.size() > 0);

        DynamoDBOperation operation = (DynamoDBOperation) operations.get(0);
        Assert.assertEquals("Invalid executed method name.", "executeGetItem", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            GetItemRequest query = (GetItemRequest) request.getQuery();
            Assert.assertEquals("No such payload detected", "test", query.getTableName());
            Assert.assertTrue("read consistency should be true", query.getConsistentRead());

            Map<String, AttributeValue> keys = query.getKey();
            Assert.assertTrue("No keys detected", keys.size() > 0);
            Assert.assertNotNull("No such payload detected", keys.get("artist"));
            Assert.assertEquals("Invalid payload value.", "Charlie", keys.get("artist").getS());
            Assert.assertNotNull("No such payload detected", keys.get("year"));
            Assert.assertEquals("Invalid payload value.", "2000", keys.get("year").getN());

            Assert.assertEquals("Invalid projection expression.", "artist, Genre", query.getProjectionExpression());
            Assert.assertEquals("Invalid query-type.", "read", request.getQueryType());
        }
    }
    @Test
    public void testGetItems3() {
        dynamo.getItemTxn3();

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("No operations detected", operations.size() > 0);

        DynamoDBOperation operation = (DynamoDBOperation) operations.get(0);
        Assert.assertEquals("Invalid executed method name.", "executeGetItem", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            GetItemRequest query = (GetItemRequest) request.getQuery();
            Assert.assertEquals("No such payload detected", "test", query.getTableName());

            Map<String, AttributeValue> keys = query.getKey();
            Assert.assertTrue("No keys detected", keys.size() > 0);
            Assert.assertNotNull("No such payload detected", keys.get("artist"));
            Assert.assertEquals("Invalid payload value.", "Charlie", keys.get("artist").getS());
            Assert.assertNotNull("No such payload detected", keys.get("year"));
            Assert.assertEquals("Invalid payload value.", "2000", keys.get("year").getN());

            Assert.assertEquals("Invalid projection expression.", "#0", query.getProjectionExpression());
            Assert.assertEquals("Invalid projection expression.", "artist, Genre", query.getExpressionAttributeNames().get("#0"));
            Assert.assertEquals("Invalid query-type.", "read", request.getQueryType());
        }
    }

    @Test
    public void testPutItems() {
        dynamo.putItemTxn();

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("No operations detected", operations.size() > 0);

        DynamoDBOperation operation = (DynamoDBOperation) operations.get(0);
        Assert.assertEquals("Invalid executed method name.", "executePutItem", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            PutItemRequest query = (PutItemRequest) request.getQuery();
            Assert.assertEquals("No such payload detected", "test", query.getTableName());

            Map<String, AttributeValue> item = query.getItem();
            Assert.assertTrue("No keys detected", item.size() > 0);
            Assert.assertNotNull("No such payload detected", item.get("artist"));
            Assert.assertEquals("Invalid payload value.", "Charlie", item.get("artist").getS());
            Assert.assertNotNull("No such payload detected", item.get("year"));
            Assert.assertEquals("Invalid payload value.", "2000", item.get("year").getN());
            Assert.assertNotNull("No such payload detected", item.get("Genre"));
            Assert.assertEquals("Invalid payload value.", "Jazz", item.get("Genre").getS());
            Assert.assertEquals("Invalid query-type.", "write", request.getQueryType());
        }
    }
    @Test
    public void testPutItems1() {
        dynamo.putItemTxn1();

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("No operations detected", operations.size() > 0);

        DynamoDBOperation operation = (DynamoDBOperation) operations.get(0);
        Assert.assertEquals("Invalid executed method name.", "executePutItem", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            PutItemRequest query = (PutItemRequest) request.getQuery();
            Assert.assertEquals("No such payload detected", "test", query.getTableName());

            Map<String, AttributeValue> item = query.getItem();
            Assert.assertTrue("No keys detected", item.size() > 0);
            Assert.assertNotNull("No such payload detected", item.get("artist"));
            Assert.assertEquals("Invalid payload value.", "Charlie", item.get("artist").getS());
            Assert.assertNotNull("No such payload detected", item.get("year"));
            Assert.assertEquals("Invalid payload value.", "2000", item.get("year").getN());
            Assert.assertNotNull("No such payload detected", item.get("Genre"));
            Assert.assertEquals("Invalid payload value.", "Jazz", item.get("Genre").getS());
            Assert.assertEquals("Invalid query-type.", "write", request.getQueryType());
        }
    }
    @Test
    public void testPutItems2() {
        dynamo.putItemTxn2();

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("No operations detected", operations.size() > 0);

        DynamoDBOperation operation = null;
        for (AbstractOperation op: operations){
            if(op.getMethodName().equals("executePutItem")){
                operation = (DynamoDBOperation) op;
            }
        }
        Assert.assertEquals("Invalid executed method name.", "executePutItem", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            PutItemRequest query = (PutItemRequest) request.getQuery();
            Assert.assertEquals("No such payload detected", "test", query.getTableName());

            Map<String, AttributeValue> item = query.getItem();
            Assert.assertTrue("No keys detected", item.size() > 0);
            Assert.assertNotNull("No such payload detected", item.get("artist"));
            Assert.assertEquals("Invalid payload value.", "Charlie", item.get("artist").getS());
            Assert.assertNotNull("No such payload detected", item.get("year"));
            Assert.assertEquals("Invalid payload value.", "2000", item.get("year").getN());
            Assert.assertNotNull("No such payload detected", item.get("Genre"));
            Assert.assertEquals("Invalid payload value.", "Jazz", item.get("Genre").getS());
            Assert.assertEquals("Invalid query-type.", "write", request.getQueryType());
        }
    }
    @Test
    public void testScanItems0() {
        dynamo.scanItemsTxn0();

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertEquals("No operations detected", 0, operations.size());
    }
    @Test
    public void testScanItems() {
        dynamo.scanItemsTxn();

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("No operations detected", operations.size() > 0);

        DynamoDBOperation operation = (DynamoDBOperation) operations.get(0);
        Assert.assertEquals("Invalid executed method name.", "executeScan", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            ScanRequest query = (ScanRequest) request.getQuery();
            Assert.assertEquals("No such payload detected", "test", query.getTableName());

            Assert.assertEquals("Invalid projection expression.", "#1", query.getProjectionExpression());
            Assert.assertEquals("Invalid filter expression.", "attribute_exists(#0)", query.getFilterExpression());
            Assert.assertTrue("Expression AttributeNames not detected", query.getExpressionAttributeNames().size() > 0);
            Assert.assertEquals("Expression Attribute-Name not detected", "Genre", query.getExpressionAttributeNames().get("#0"));
            Assert.assertEquals("Expression Attribute-Name not detected", "artist, Genre", query.getExpressionAttributeNames().get("#1"));
            Assert.assertEquals("Invalid query-type.", "read", request.getQueryType());
        }
    }
    @Test
    public void testScanItems1() {
        dynamo.scanItemsTxn1();

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("No operations detected", operations.size() > 0);

        DynamoDBOperation operation = (DynamoDBOperation) operations.get(0);
        Assert.assertEquals("Invalid executed method name.", "executeScan", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            ScanRequest query = (ScanRequest) request.getQuery();
            Assert.assertEquals("No such payload detected", "test", query.getTableName());

            Assert.assertEquals("Invalid projection expression.", "artist, Genre", query.getProjectionExpression());
            Assert.assertEquals("Invalid filter expression.", "Genre = :val", query.getFilterExpression());
            Assert.assertTrue("Expression AttributeNames not detected", query.getExpressionAttributeValues().size() > 0);
            Assert.assertEquals("Expression Attribute-Name not detected", "Jazz", query.getExpressionAttributeValues().get(":val").getS());
            Assert.assertEquals("Invalid query-type.", "read", request.getQueryType());
        }
    }
    @Test
    public void testUpdateItems() {
        dynamo.updateItemsTxn();

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("No operations detected", operations.size() > 0);

        DynamoDBOperation operation = null;
        for (AbstractOperation op: operations){
            if(op.getMethodName().equals("executeUpdateItem")){
                operation = (DynamoDBOperation) op;
            }
        }
        Assert.assertEquals("Invalid executed method name.", "executeUpdateItem", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            UpdateItemRequest query = (UpdateItemRequest) request.getQuery();
            Assert.assertEquals("No such payload detected", "test", query.getTableName());
            Assert.assertTrue("No keys detected", query.getKey().size() > 0);

            Assert.assertNotNull("No such keys detected", query.getKey().get("artist"));
            Assert.assertEquals("Invalid payload value.", "Charlie",query.getKey().get("artist").getS());
            Assert.assertNotNull("No such keys detected", query.getKey().get("year"));
            Assert.assertEquals("Invalid payload value.", "2000",query.getKey().get("year").getN());

            Map<String, AttributeValueUpdate> update = query.getAttributeUpdates();
            Assert.assertTrue("No such AttributeUpdates map detected", update.size() > 0);
            Assert.assertNotNull("No such AttributeUpdates map detected", update.get("Genre"));
            AttributeValueUpdate updateAttri = update.get("Genre");
            Assert.assertNotNull("No such keys detected", updateAttri.getValue());
            Assert.assertEquals("No such AttributeUpdates value detected", "Classic", updateAttri.getValue().getS());
            Assert.assertEquals("No such action detected", "PUT", updateAttri.getAction());
            Assert.assertEquals("Invalid query-type.", "update", request.getQueryType());
        }
    }
    @Test
    public void testUpdateItems1() {
        dynamo.updateItemsTxn1();

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("No operations detected", operations.size() > 0);

        DynamoDBOperation operation = null;
        for (AbstractOperation op: operations){
            if(op.getMethodName().equals("executeUpdateItem")){
                operation = (DynamoDBOperation) op;
            }
        }
        Assert.assertEquals("Invalid executed method name.", "executeUpdateItem", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            UpdateItemRequest query = (UpdateItemRequest) request.getQuery();
            Assert.assertEquals("No such payload detected", "test", query.getTableName());
            Assert.assertTrue("No keys detected", query.getKey().size() > 0);

            Assert.assertNotNull("No such keys detected", query.getKey().get("artist"));
            Assert.assertEquals("Invalid payload value.", "Charlie",query.getKey().get("artist").getS());
            Assert.assertNotNull("No such keys detected", query.getKey().get("year"));
            Assert.assertEquals("Invalid payload value.", "2000",query.getKey().get("year").getN());

            Assert.assertEquals("Invalid update expression", "set Genre = :newVal", query.getUpdateExpression());
            Assert.assertEquals("Invalid condition expression", "Genre = :val", query.getConditionExpression());

            Assert.assertNotNull("No Expression-AttributeValues detected.", query.getExpressionAttributeValues());
            Assert.assertNotNull("No such Attribute-value detected.", query.getExpressionAttributeValues().get(":val"));
            Assert.assertEquals("No such Attribute-value detected.", "Jazz", query.getExpressionAttributeValues().get(":val").getS());
            Assert.assertNotNull("No such Attribute-value detected.", query.getExpressionAttributeValues().get(":newVal"));
            Assert.assertEquals("No such Attribute-value detected.", "Classic", query.getExpressionAttributeValues().get(":newVal").getS());
            Assert.assertEquals("Invalid query-type.", "update", request.getQueryType());
        }
    }
    @Test
    public void testUpdateItems2() {
        dynamo.updateItemsTxn2();

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("No operations detected", operations.size() > 0);

        DynamoDBOperation operation = null;
        for (AbstractOperation op: operations){
            if(op.getMethodName().equals("executeUpdateItem")){
                operation = (DynamoDBOperation) op;
            }
        }
        Assert.assertEquals("Invalid executed method name.", "executeUpdateItem", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            UpdateItemRequest query = (UpdateItemRequest) request.getQuery();
            Assert.assertEquals("No such payload detected", "test", query.getTableName());
            Assert.assertTrue("No keys detected", query.getKey().size() > 0);

            Assert.assertNotNull("No such keys detected", query.getKey().get("artist"));
            Assert.assertEquals("Invalid payload value.", "Charlie",query.getKey().get("artist").getS());
            Assert.assertNotNull("No such keys detected", query.getKey().get("year"));
            Assert.assertEquals("Invalid payload value.", "2000",query.getKey().get("year").getN());

            Map<String, AttributeValueUpdate> update = query.getAttributeUpdates();
            Assert.assertTrue("No such AttributeUpdates map detected", update.size() > 0);
            Assert.assertNotNull("No such AttributeUpdates map detected", update.get("Genre"));
            AttributeValueUpdate updateAttri = update.get("Genre");
            Assert.assertNotNull("No such keys detected", updateAttri.getValue());
            Assert.assertEquals("No such AttributeUpdates value detected", "Classic", updateAttri.getValue().getS());
            Assert.assertEquals("No such action detected", "PUT", updateAttri.getAction());
            Assert.assertEquals("Invalid query-type.", "update", request.getQueryType());
        }
    }
    @Test
    public void testUpdateItems3() {
        dynamo.updateItemsTxn3();

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("No operations detected", operations.size() > 0);

        DynamoDBOperation operation = null;
        for (AbstractOperation op: operations){
            if(op.getMethodName().equals("executeUpdateItem")){
                operation = (DynamoDBOperation) op;
            }
        }
        Assert.assertEquals("Invalid executed method name.", "executeUpdateItem", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            UpdateItemRequest query = (UpdateItemRequest) request.getQuery();
            Assert.assertEquals("No such payload detected", "test", query.getTableName());
            Assert.assertTrue("No keys detected", query.getKey().size() > 0);

            Assert.assertNotNull("No such keys detected", query.getKey().get("artist"));
            Assert.assertEquals("Invalid payload value.", "Charlie",query.getKey().get("artist").getS());
            Assert.assertNotNull("No such keys detected", query.getKey().get("year"));
            Assert.assertEquals("Invalid payload value.", "2000",query.getKey().get("year").getN());

            Assert.assertEquals("Invalid update expression", "set Genre = :newVal", query.getUpdateExpression());

            Assert.assertNotNull("No Expression-AttributeValues detected.", query.getExpressionAttributeValues());
            Assert.assertNotNull("No such Attribute-value detected.", query.getExpressionAttributeValues().get(":newVal"));
            Assert.assertEquals("No such Attribute-value detected.", "Classic", query.getExpressionAttributeValues().get(":newVal").getS());
            Assert.assertEquals("Invalid query-type.", "update", request.getQueryType());
        }
    }
    @Test
    public void testUpdateItems4() {
        dynamo.updateItemsTxn4();

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("No operations detected", operations.size() > 0);

        DynamoDBOperation operation = null;
        for (AbstractOperation op: operations){
            if(op.getMethodName().equals("executeUpdateItem")){
                operation = (DynamoDBOperation) op;
            }
        }
        Assert.assertEquals("Invalid executed method name.", "executeUpdateItem", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            UpdateItemRequest query = (UpdateItemRequest) request.getQuery();
            Assert.assertEquals("No such payload detected", "test", query.getTableName());
            Assert.assertTrue("No keys detected", query.getKey().size() > 0);

            Assert.assertNotNull("No such keys detected", query.getKey().get("artist"));
            Assert.assertEquals("Invalid payload value.", "Charlie",query.getKey().get("artist").getS());
            Assert.assertNotNull("No such keys detected", query.getKey().get("year"));
            Assert.assertEquals("Invalid payload value.", "2000",query.getKey().get("year").getN());

            Assert.assertEquals("Invalid update expression", "set Genre = :newVal", query.getUpdateExpression());
            Assert.assertEquals("Invalid condition expression", "Genre = :val", query.getConditionExpression());

            Assert.assertNotNull("No Expression-AttributeValues detected.", query.getExpressionAttributeValues());
            Assert.assertNotNull("No such Attribute-value detected.", query.getExpressionAttributeValues().get(":val"));
            Assert.assertEquals("No such Attribute-value detected.", "Jazz", query.getExpressionAttributeValues().get(":val").getS());
            Assert.assertNotNull("No such Attribute-value detected.", query.getExpressionAttributeValues().get(":newVal"));
            Assert.assertEquals("No such Attribute-value detected.", "Classic", query.getExpressionAttributeValues().get(":newVal").getS());
            Assert.assertEquals("Invalid query-type.", "update", request.getQueryType());
        }
    }

    @Test
    public void testBatchGetAsync0() throws Exception{
        dynamo.batchGetAsyncTxn0();
        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertEquals("operations should not be detected", 0, operations.size());
    }
    @Test
    public void testBatchGetAsync() throws Exception{
        dynamo.batchGetAsyncTxn();
        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("no operation detected", operations.size() > 0);

        DynamoDBOperation operation = (DynamoDBOperation) operations.get(0);
        Assert.assertEquals("Invalid event category.", VulnerabilityCaseType.DYNAMO_DB_COMMAND, operation.getCaseType());
        Assert.assertEquals("Invalid executed method name.", "executeBatchGetItem", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            KeysAndAttributes attributes = (KeysAndAttributes) request.getQuery();
            List<Map<String, AttributeValue>> keys = attributes.getKeys();

            Assert.assertTrue("No keys detected", keys.size() > 0);
            Assert.assertNotNull("No such payload detected", keys.get(0).get("artist"));
            Assert.assertEquals("Invalid payload value.", "Charlie", keys.get(0).get("artist").getS());
            Assert.assertNotNull("No such payload detected", keys.get(0).get("year"));
            Assert.assertEquals("Invalid payload value.", "2000", keys.get(0).get("year").getN());

            Assert.assertEquals("Invalid projection expression.", "artist, Genre", attributes.getProjectionExpression());
            Assert.assertEquals("Invalid query-type.", "read", request.getQueryType());
        }
    }
    @Test
    public void testBatchWriteAsync() throws Exception {
        dynamo.batchWriteTxnAsync();
        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("no operation detected", operations.size() > 0);

        DynamoDBOperation operation = (DynamoDBOperation) operations.get(0);
        Assert.assertEquals("Invalid event category.", VulnerabilityCaseType.DYNAMO_DB_COMMAND, operation.getCaseType());
        Assert.assertEquals("Invalid executed method name.", "executeBatchWriteItem", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            PutRequest query = (PutRequest) request.getQuery();

            Assert.assertNotNull("No such payload detected", query.getItem().get("artist"));
            Assert.assertEquals("Invalid payload value.", "Charlie",query.getItem().get("artist").getS());
            Assert.assertNotNull("No such payload detected", query.getItem().get("year"));
            Assert.assertEquals("Invalid payload value.", "2000", query.getItem().get("year").getN());
            Assert.assertNotNull("No such payload detected", query.getItem().get("Genre"));
            Assert.assertEquals("Invalid payload value.", "Jazz",query.getItem().get("Genre").getS());

            Assert.assertEquals("Invalid query-type.", "write", request.getQueryType());
        }
    }
    @Test
    public void testDeleteItemAsync() throws Exception {
        dynamo.deleteTxnAsync();
        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("no operation detected", operations.size() > 0);

        DynamoDBOperation operation = null;
        for (AbstractOperation op: operations){
            if(op.getMethodName().equals("executeDeleteItem")){
                operation = (DynamoDBOperation) op;
            }
        }
        Assert.assertEquals("Invalid event category.", VulnerabilityCaseType.DYNAMO_DB_COMMAND, operation.getCaseType());
        Assert.assertEquals("Invalid executed method name.", "executeDeleteItem", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            DeleteItemRequest query = (DeleteItemRequest) request.getQuery();
            Assert.assertEquals("No such payload detected", "test", query.getTableName());
            Assert.assertTrue("No keys detected", query.getKey().size() > 0);

            Assert.assertNotNull("No such keys detected", query.getKey().get("artist"));
            Assert.assertEquals("Invalid payload value.", "Charlie",query.getKey().get("artist").getS());
            Assert.assertNotNull("No such keys detected", query.getKey().get("year"));
            Assert.assertEquals("Invalid payload value.", "2000",query.getKey().get("year").getN());

            Assert.assertEquals("Invalid Condition-Expression.", "Genre = :val", query.getConditionExpression());
            Assert.assertNotNull("No Expression-AttributeValues detected.", query.getExpressionAttributeValues());
            Assert.assertNotNull("No such Attribute-value detected.", query.getExpressionAttributeValues().get(":val"));
            Assert.assertEquals("No such Attribute-value detected.", "Jazz", query.getExpressionAttributeValues().get(":val").getS());
            Assert.assertEquals("Invalid query-type.", "delete", request.getQueryType());
        }
    }
    @Test
    public void testQueryAsync() throws Exception {
        dynamo.queryTxnAsync();
        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("no operation detected", operations.size() > 0);

        DynamoDBOperation operation = null;
        for (AbstractOperation op: operations){
            if(op.getMethodName().equals("executeQuery")){
                operation = (DynamoDBOperation) op;
            }
        }
        Assert.assertEquals("Invalid event category.", VulnerabilityCaseType.DYNAMO_DB_COMMAND, operation.getCaseType());
        Assert.assertEquals("Invalid executed method name.", "executeQuery", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            QueryRequest query = (QueryRequest) request.getQuery();
            Assert.assertEquals("No such payload detected", "test", query.getTableName());

            Assert.assertEquals("Invalid Condition-Expression.", "artist = :val", query.getKeyConditionExpression());
            Assert.assertNotNull("No Expression-AttributeValues detected.", query.getExpressionAttributeValues());
            Assert.assertNotNull("No such Attribute-value detected.", query.getExpressionAttributeValues().get(":val"));
            Assert.assertEquals("No such Attribute-value detected.", "Charlie", query.getExpressionAttributeValues().get(":val").getS());
            Assert.assertEquals("Invalid query-type.", "read", request.getQueryType());
        }
    }
    @Test
    public void testGetItemsAsync() throws Exception {
        dynamo.getItemsTxnAsync();
        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("no operation detected", operations.size() > 0);

        DynamoDBOperation operation = null;
        for (AbstractOperation op: operations){
            if(op.getMethodName().equals("executeGetItem")){
                operation = (DynamoDBOperation) op;
            }
        }
        Assert.assertEquals("Invalid executed method name.", "executeGetItem", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            GetItemRequest query = (GetItemRequest) request.getQuery();
            Assert.assertEquals("No such payload detected", "test", query.getTableName());
            Assert.assertTrue("read consistency should be true", query.getConsistentRead());

            Map<String, AttributeValue> keys = query.getKey();
            Assert.assertTrue("No keys detected", keys.size() > 0);
            Assert.assertNotNull("No such payload detected", keys.get("artist"));
            Assert.assertEquals("Invalid payload value.", "Charlie", keys.get("artist").getS());
            Assert.assertNotNull("No such payload detected", keys.get("year"));
            Assert.assertEquals("Invalid payload value.", "2000", keys.get("year").getN());

            Assert.assertEquals("Invalid projection expression.", "artist, Genre", query.getProjectionExpression());
            Assert.assertEquals("Invalid query-type.", "read", request.getQueryType());
        }
    }
    @Test
    public void testPutItemsAsync() throws Exception {
        dynamo.putItemsTxnAsync();
        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("no operation detected", operations.size() > 0);

        DynamoDBOperation operation = (DynamoDBOperation) operations.get(0);
        Assert.assertEquals("Invalid executed method name.", "executePutItem", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            PutItemRequest query = (PutItemRequest) request.getQuery();
            Assert.assertEquals("No such payload detected", "test", query.getTableName());

            Map<String, AttributeValue> item = query.getItem();
            Assert.assertTrue("No keys detected", item.size() > 0);
            Assert.assertNotNull("No such payload detected", item.get("artist"));
            Assert.assertEquals("Invalid payload value.", "Charlie", item.get("artist").getS());
            Assert.assertNotNull("No such payload detected", item.get("year"));
            Assert.assertEquals("Invalid payload value.", "2000", item.get("year").getN());
            Assert.assertNotNull("No such payload detected", item.get("Genre"));
            Assert.assertEquals("Invalid payload value.", "Jazz", item.get("Genre").getS());
            Assert.assertEquals("Invalid query-type.", "write", request.getQueryType());
        }
    }
    @Test
    public void testScanAsync() throws Exception {
        dynamo.scanTxnAsync();
        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("no operation detected", operations.size() > 0);

        DynamoDBOperation operation = (DynamoDBOperation) operations.get(0);
        Assert.assertEquals("Invalid executed method name.", "executeScan", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            ScanRequest query = (ScanRequest) request.getQuery();
            Assert.assertEquals("No such payload detected", "test", query.getTableName());
            Assert.assertEquals("Invalid projection expression.", "artist, Genre", query.getProjectionExpression());
            Assert.assertEquals("Invalid query-type.", "read", request.getQueryType());
        }
    }
    @Test
    public void testUpdateItemAsync() throws Exception {
        dynamo.updateItemTxnAsync();
        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("no operation detected", operations.size() > 0);

        DynamoDBOperation operation = null;
        for (AbstractOperation op: operations){
            if(op.getMethodName().equals("executeUpdateItem")){
                operation = (DynamoDBOperation) op;
            }
        }
        Assert.assertEquals("Invalid executed method name.", "executeUpdateItem", operation.getMethodName());
        Assert.assertEquals("Invalid operation category.", DynamoDBOperation.Category.DQL, operation.getCategory());

        for(DynamoDBRequest request: operation.getPayload()) {
            UpdateItemRequest query = (UpdateItemRequest) request.getQuery();
            Assert.assertEquals("No such payload detected", "test", query.getTableName());
            Assert.assertTrue("No keys detected", query.getKey().size() > 0);

            Assert.assertNotNull("No such keys detected", query.getKey().get("artist"));
            Assert.assertEquals("Invalid payload value.", "Charlie",query.getKey().get("artist").getS());
            Assert.assertNotNull("No such keys detected", query.getKey().get("year"));
            Assert.assertEquals("Invalid payload value.", "2000",query.getKey().get("year").getN());

            Assert.assertEquals("Invalid update expression", "set Genre = :newVal", query.getUpdateExpression());
            Assert.assertEquals("Invalid condition expression", "Genre = :val", query.getConditionExpression());

            Assert.assertNotNull("No Expression-AttributeValues detected.", query.getExpressionAttributeValues());
            Assert.assertNotNull("No such Attribute-value detected.", query.getExpressionAttributeValues().get(":val"));
            Assert.assertEquals("No such Attribute-value detected.", "Jazz", query.getExpressionAttributeValues().get(":val").getS());
            Assert.assertNotNull("No such Attribute-value detected.", query.getExpressionAttributeValues().get(":newVal"));
            Assert.assertEquals("No such Attribute-value detected.", "Classic", query.getExpressionAttributeValues().get(":newVal").getS());
            Assert.assertEquals("Invalid query-type.", "update", request.getQueryType());
        }
    }
}
