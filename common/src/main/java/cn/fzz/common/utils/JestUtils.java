package cn.fzz.common.utils;

import java.util.ArrayList;
import java.util.List;

import cn.fzz.elasticsearch.model.User;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Bulk;
import io.searchbox.core.BulkResult;
import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.mapping.GetMapping;
import io.searchbox.indices.mapping.PutMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

public class JestUtils {
    private static Logger logger = LoggerFactory.getLogger(JestUtils.class);

    //Redis服务器IP
    private static String ADDR = "127.0.0.1";

    //Redis的端口号
    private static int PORT = 9200;

    private static JestClient jestClient;

    private String indexName;

    private String typeName;

    static {
        jestClient = getJestClient();
    }

    private static  JestClient getJestClient() {
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig.Builder(ADDR + ":" + PORT).connTimeout(6000)
                .readTimeout(6000).multiThreaded(true).build());
        return factory.getObject();
    }

    public void insertBatch() {
        List<Object> objs = new ArrayList<Object>();
        objs.add(new User("张三", "123456"));
        objs.add(new User("李四", "123456"));
        objs.add(new User("王五", "123456"));
        boolean result = false;
        try {
            result = insertBatch(jestClient,indexName, typeName,objs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("批量新增:"+result);
    }


    /**
     * 全文搜索
     */
    public void searchAll() {
        String query ="tb";
        try {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.queryStringQuery(query));
            //分页设置
            searchSourceBuilder.from(0).size(2);
            System.out.println("全文搜索查询语句:"+searchSourceBuilder.toString());
            System.out.println("全文搜索返回结果:"+search(jestClient,indexName, typeName, searchSourceBuilder.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 精确搜索
     */
    public void search() {
        try {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.termQuery("login_name", "test"));
            System.out.println("精确搜索查询语句:"+searchSourceBuilder.toString());
            System.out.println("精确搜索返回结果:"+search(jestClient,indexName, typeName, searchSourceBuilder.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 区间搜索
     */
    public void searchPart() {
        String createTime="create_time";
        String from="2016-8-21 06:11:32";
        String to="2020-8-21 06:11:32";

        try {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.rangeQuery(createTime).gte(from).lte(to));
            System.out.println("区间搜索语句:"+searchSourceBuilder.toString());
            System.out.println("区间搜索返回结果:"+search(jestClient,indexName, typeName, searchSourceBuilder.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 创建索引
     * @param indexName
     * @return
     * @throws Exception
     */
    public boolean createIndex(JestClient jestClient,String indexName) throws Exception {
        JestResult jr = jestClient.execute(new CreateIndex.Builder(indexName).build());
        return jr.isSucceeded();
    }

    /**
     * 新增数据
     * @param indexName
     * @param typeName
     * @param source
     * @return
     * @throws Exception
     */
    public boolean insert(JestClient jestClient,String indexName, String typeName, String source) throws Exception {
        PutMapping putMapping = new PutMapping.Builder(indexName, typeName, source).build();
        JestResult jr = jestClient.execute(putMapping);
        return jr.isSucceeded();
    }


    /**
     * 查询数据
     * @param indexName
     * @param typeName
     * @return
     * @throws Exception
     */
    public static String getIndexMapping(JestClient jestClient,String indexName, String typeName) throws Exception {
        GetMapping getMapping = new GetMapping.Builder().addIndex(indexName).addType(typeName).build();
        JestResult jr =jestClient.execute(getMapping);
        return jr.getJsonString();
    }



    /**
     * 批量新增数据
     * @param indexName
     * @param typeName
     * @param objs
     * @return
     * @throws Exception
     */
    public static boolean insertBatch(JestClient jestClient,String indexName, String typeName, List<Object> objs) throws Exception {
        Bulk.Builder bulk = new Bulk.Builder().defaultIndex(indexName).defaultType(typeName);
        for (Object obj : objs) {
            Index index = new Index.Builder(obj).build();
            bulk.addAction(index);
        }
        BulkResult br = jestClient.execute(bulk.build());
        return br.isSucceeded();
    }

    /**
     * 全文搜索
     * @param indexName
     * @param typeName
     * @param query
     * @return
     * @throws Exception
     */
    public static String search(JestClient jestClient,String indexName, String typeName, String query) throws Exception {
        Search search = new Search.Builder(query)
                .addIndex(indexName)
                .addType(typeName)
                .build();
        JestResult jr = jestClient.execute(search);
//          System.out.println("--"+jr.getJsonString());
//          System.out.println("--"+jr.getSourceAsObject(User.class));
        return jr.getSourceAsString();
    }





    /**
     * 删除索引
     * @param indexName
     * @return
     * @throws Exception
     */
    public boolean delete(JestClient jestClient,String indexName) throws Exception {
        JestResult jr = jestClient.execute(new DeleteIndex.Builder(indexName).build());
        return jr.isSucceeded();
    }

    /**
     * 删除数据
     * @param indexName
     * @param typeName
     * @param id
     * @return
     * @throws Exception
     */
    public boolean delete(JestClient jestClient,String indexName, String typeName, String id) throws Exception {
        DocumentResult dr = jestClient.execute(new Delete.Builder(id).index(indexName).type(typeName).build());
        return dr.isSucceeded();
    }

    public JestUtils() {
    }

    public JestUtils(String indexName, String typeName) {
        this.indexName = indexName;
        this.typeName = typeName;
    }
}
