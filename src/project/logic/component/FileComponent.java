package project.logic.component;

import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.logging.LogFactory.getLog;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import drizzt.common.handler.FreeMarkerExtendHandler;
import drizzt.common.tools.FreeMarkerUtils;
import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;

/**
 * 
 * FileComponent 文件操作组件
 *
 */
public class FileComponent {
    private String staticFileDirectory;
    private String templateLoaderPath;
    private String dataFilePath;
    private String uploadFilePath;
    private String basePath;
    private String sitePath;
    private String cmsPath;
    private String uploadPath;
    public static final String DEFAULT_STATIC_FILE_DIRECTORY = "static/";
    public static final String DEFAULT_TEMPLATE_LOADER_PATH = "WEB-INF/template/";
    public static final String DEFAULT_DATA_FILE_PATH = "WEB-INF/data/";
    public static final String DEFAULT_UPLOAD_FILE_PATH = "resource/upload/";
    public static final String DEFAULT_PAGE_BREAK_TAG = "_page_break_tag_";
    public static final String INCLUDE_DIR = "include";
    public static final String FTL_DIR = "ftl";
    public static final String INCLUDE_PATH = "/include/";
    private static final String METADATA_FILE = "/metadata.data";

    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private FreeMarkerExtendHandler freeMarkerExtendHandler;
    private Configuration configuration;
    private final Log log = getLog(getClass());

    /**
     * 处理字符串模板
     * 
     * @param template
     * @param model
     * @return
     * @throws IOException
     * @throws TemplateException
     */
    public String dealStringTemplate(String template, ModelMap model) throws IOException, TemplateException {
        return FreeMarkerUtils.makeStringByString(template, configuration, model);
    }


    /**
     * 创建静态化页面
     * 
     * @param templatePath
     * @param filePath
     * @param model
     * @return
     */
    public StaticResult createStaticFile(String templatePath, String filePath, ModelMap model) {
        try {
            if (isNotBlank(filePath)) {
                if (null != model) {
                    model = (ModelMap) model.clone();
                }
                filePath = FreeMarkerUtils.makeStringByString(filePath, configuration, model);
                if (isNotBlank(templatePath)) {
                    model.put("url", filePath);
                    FreeMarkerUtils.makeFileByFile(templatePath, getStaticFilePath(filePath), configuration, model);
                }
            }
            return new StaticResult(true, filePath);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new StaticResult();
        }
    }

    /**
     * 获取目录下文件列表
     * 
     * @param dirPath
     * @param exclude
     *            是否包含ftl、include目录
     * @return
     */
    public List<FileInfo> getFileList(String dirPath, boolean exclude) {
        List<FileInfo> dirList = new ArrayList<FileInfo>();
        List<FileInfo> fileList = new ArrayList<FileInfo>();
        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(getTemplateFilePath(dirPath)));
            Map<String, Map<String, Object>> metadataMap = getMetadata(getTemplateFilePath(dirPath));
            for (Path entry : stream) {
                BasicFileAttributes attrs = Files.readAttributes(entry, BasicFileAttributes.class);
                String fileName = entry.getFileName().toString();
                String alias = null;
                String path = fileName;
                Map<String, Object> infoMap = metadataMap.get(fileName);
                if (null != infoMap) {
                    if (null != infoMap.get("alias")) {
                        alias = (String) infoMap.get("alias");
                    }
                    if (null != infoMap.get("path")) {
                        path = (String) infoMap.get("path");
                    }
                }
                if (attrs.isDirectory()) {
                    if (!exclude || (!INCLUDE_DIR.equalsIgnoreCase(fileName) && !FTL_DIR.equalsIgnoreCase(fileName))) {
                        dirList.add(new FileInfo(fileName, path, alias, true, attrs));
                    }
                } else {
                    if (!"metadata.data".equalsIgnoreCase(fileName)) {
                        fileList.add(new FileInfo(fileName, path, alias, false, attrs));
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        dirList.addAll(fileList);
        return dirList;
    }

    /**
     * 写入文件
     * 
     * @param filePath
     * @param content
     * @return
     */
    public boolean createPage(String filePath, String content) {
        try {
            File file = new File(getTemplateFilePath(filePath));
            if (!file.exists()) {
                writeStringToFile(file, content, "UTF-8");
                return true;
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return false;
    }

    /**
     * 更新元数据
     * 
     * @param filePath
     * @param map
     * @return
     */
    public boolean updateMetadata(String filePath, Map<String, Object> map) {
        try {
            File file = new File(getTemplateFilePath(filePath));
            if (file.exists()) {
                String dirPath = file.getParent();
                Map<String, Map<String, Object>> metadataMap = getMetadata(dirPath);
                metadataMap.put(file.getName(), map);
                saveMetadata(dirPath, metadataMap);
                return true;
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return false;
    }

    /**
     * 删除文件或目录
     * 
     * @param filePath
     * @return
     */
    public boolean deletePage(String filePath) {
        File file = new File(getTemplateFilePath(filePath));
        if (file.exists()) {
            deleteQuietly(file);
            return true;
        }
        return false;
    }

    /**
     * 保存文件内容
     * 
     * @param filePath
     * @param content
     * @return
     */
    public boolean saveContent(String filePath, String content) {
        try {
            File file = new File(getTemplateFilePath(filePath));
            if (file.exists()) {
                writeStringToFile(file, content, "UTF-8");
                return true;
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return false;
    }

    /**
     * 静态化页面
     * 
     * @param filePath
     * @return
     */
    public StaticResult staticPage(String filePath) {
        File file = new File(getTemplateFilePath(filePath));
        if (null != file && file.exists()) {
            Map<String, Object> map = getTemplateMetadata(filePath);
            if (filePath.startsWith(INCLUDE_PATH)) {
                return staticPlace(filePath.substring(INCLUDE_PATH.length() - 1));
            } else if (null != map.get("path")) {
                ModelMap model = new ModelMap();
                model.addAllAttributes(map);
                return createStaticFile(filePath, (String) map.get("path"), model);
            }
        }
        return new StaticResult(true, "");
    }

    /**
     * 获取模板文件内容
     * 
     * @param filePath
     * @return
     */
    public String getContent(String filePath) {
        try {
            File file = new File(getTemplateFilePath(filePath));
            return readFileToString(file, "UTF-8");
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 静态化页面片段
     * 
     * @param filePath
     * @return
     */
    public StaticResult staticPlace(String filePath) {
        if (isNotBlank(filePath)) {
            List<Map<String, Object>> dataList = getListData(filePath);
            ModelMap map = new ModelMap();
            map.put("dataList", dataList);
            filePath = INCLUDE_DIR + filePath;
            return createStaticFile(filePath, filePath, map);
        }
        return new StaticResult();
    }

    /**
     * 保存推荐位数据
     * 
     * @param filePath
     * @param data
     * @throws IOException
     */
    public void saveData(String filePath, Map<String, Object> data) throws IOException {
        List<Map<String, Object>> dataList = getListData(filePath);
        dataList.add(data);
        saveData(filePath, dataList);
    }

    /**
     * 保存哈希表数据
     * 
     * @param filePath
     * @param data
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    public void saveMapData(String filePath, Map<String, Object> data) throws JsonGenerationException, JsonMappingException,
            IOException {
        File file = new File(getDataFilePath(filePath));
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        objectMapper.writeValue(file, data);
    }

    /**
     * 更新推荐位数据
     * 
     * @param filePath
     * @param createDate
     * @param data
     * @throws IOException
     */
    public void updateData(String filePath, Long createDate, Map<String, Object> data) throws IOException {
        if (null != createDate) {
            List<Map<String, Object>> dataList = getListData(filePath);
            int i = 0;
            for (Map<String, Object> map : dataList) {
                if (createDate.equals(map.get("createDate"))) {
                    dataList.set(i, data);
                    break;
                }
                i++;
            }
            saveData(filePath, dataList);
        }
    }

    /**
     * 删除推荐位数据
     * 
     * @param filePath
     * @param createDate
     * @throws IOException
     */
    public void deleteData(String filePath, Long createDate) throws IOException {
        if (null != createDate) {
            List<Map<String, Object>> dataList = getListData(filePath);
            int i = 0;
            for (Map<String, Object> map : dataList) {
                if (createDate.equals(map.get("createDate"))) {
                    dataList.remove(i);
                    break;
                }
                i++;
            }
            saveData(filePath, dataList);
        }
    }

    /**
     * 获取元数据
     * 
     * @param filePath
     * @return
     */
    public Map<String, Object> getTemplateMetadata(String filePath) {
        File file = new File(getTemplateFilePath(filePath));
        Map<String, Object> map = null;
        if (null != file && file.exists()) {
            map = getMetadata(file.getParent()).get(file.getName());
        }
        if (null == map) {
            map = new LinkedHashMap<String, Object>();
        }
        return map;
    }

    /**
     * 获取文件名
     * 
     * @param suffix
     * @return
     */
    public String getUploadFileName(String suffix) {
        return new SimpleDateFormat("yyyy/MM/dd/HH-mm-ssSSSS").format(new Date()) + new Random().nextInt() + suffix;
    }

    /**
     * 获取文件后缀
     * 
     * @param originalFilename
     * @return
     */
    public String getSuffix(String originalFilename) {
        return originalFilename.substring(originalFilename.lastIndexOf("."), originalFilename.length());
    }

    /**
     * 上传文件
     * 
     * @param file
     * @param fileName
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    public String upload(MultipartFile file, String fileName) throws IllegalStateException, IOException {
        File dest = new File(getUploadFilePath(fileName));
        dest.getParentFile().mkdirs();
        file.transferTo(dest);
        return dest.getName();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Map<String, Object>> getMetadata(String dirPath) {
        try {
            return objectMapper.readValue(new File(dirPath + METADATA_FILE), Map.class);
        } catch (Exception e) {
            return new LinkedHashMap<String, Map<String, Object>>();
        }
    }

    /**
     * 保存元数据
     * 
     * @param dirPath
     * @param metadataMap
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    private void saveMetadata(String dirPath, Map<String, Map<String, Object>> metadataMap) throws JsonGenerationException,
            JsonMappingException, IOException {
        File file = new File(dirPath + METADATA_FILE);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        objectMapper.writeValue(file, metadataMap);
    }

    private void saveData(String filePath, List<Map<String, Object>> dataList) throws JsonGenerationException,
            JsonMappingException, IOException {
        File file = new File(getDataFilePath(filePath));
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        objectMapper.writeValue(file, dataList);
    }

    /**
     * 获取列表数据
     * 
     * @param filePath
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getListData(String filePath) {
        List<Map<String, Object>> dataList = null;
        try {
            dataList = objectMapper.readValue(new File(getDataFilePath(filePath)), List.class);
        } catch (IOException e) {
            dataList = new ArrayList<Map<String, Object>>();
        }
        return dataList;
    }

    /**
     * 获取哈希表数据
     * 
     * @param filePath
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getMapData(String filePath) {
        try {
            return objectMapper.readValue(new File(filePath), Map.class);
        } catch (Exception e) {
            return new LinkedHashMap<String, Object>();
        }
    }

    /**
     * @param templatePath
     * @return
     */
    public String getDataFilePath(String templatePath) {
        return dataFilePath + templatePath + ".data";
    }

    /**
     * @param filePath
     * @return
     */
    public String getStaticFilePath(String filePath) {
        return staticFileDirectory + filePath;
    }

    /**
     * @param templatePath
     * @return
     */
    public String getTemplateFilePath(String templatePath) {
        return templateLoaderPath + templatePath;
    }

    /**
     * @param filePath
     * @return
     */
    public String getUploadFilePath(String filePath) {
        return uploadFilePath + filePath;
    }

    @Autowired
    private void setFreeMarkerConfigurer(FreeMarkerConfigurer freeMarkerConfigurer) {
        configuration = (Configuration) freeMarkerConfigurer.getConfiguration().clone();
        try {
            configuration.setDirectoryForTemplateLoading(new File(getTemplateFilePath("")));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        configuration.setAutoImports(new HashMap<String, String>());
        configuration.setAutoIncludes(new ArrayList<String>());
        try {
            configuration.setAllSharedVariables(new SimpleHash(freeMarkerExtendHandler.getFreemarkerVariables(),
                    freeMarkerConfigurer.getConfiguration().getObjectWrapper()));
        } catch (TemplateModelException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * @param staticFileDirectory
     *            the staticFileDirectory to set
     */
    public void setStaticFileDirectory(String staticFileDirectory) {
        if (isNotBlank(staticFileDirectory)) {
            if (!(staticFileDirectory.endsWith("/") || staticFileDirectory.endsWith("\\"))) {
                staticFileDirectory += "/";
            }
        } else {
            staticFileDirectory = basePath + DEFAULT_STATIC_FILE_DIRECTORY;
        }
        this.staticFileDirectory = staticFileDirectory;
    }

    /**
     * @param templateLoaderPath
     *            the templateLoaderPath to set
     */
    public void setTemplateLoaderPath(String templateLoaderPath) {
        if (isNotBlank(templateLoaderPath)) {
            if (!(templateLoaderPath.endsWith("/") || templateLoaderPath.endsWith("\\"))) {
                templateLoaderPath += "/";
            }
        } else {
            templateLoaderPath = basePath + DEFAULT_TEMPLATE_LOADER_PATH;
        }
        this.templateLoaderPath = templateLoaderPath;
    }

    /**
     * @param dataFilePath
     *            the dataFilePath to set
     */
    public void setDataFilePath(String dataFilePath) {
        if (isNotBlank(dataFilePath)) {
            if (!(dataFilePath.endsWith("/") || dataFilePath.endsWith("\\"))) {
                dataFilePath += "/";
            }
        }
        this.dataFilePath = dataFilePath;
    }

    /**
     * @param uploadFilePath
     *            the uploadFilePath to set
     */
    public void setUploadFilePath(String uploadFilePath) {
        if (isNotBlank(uploadFilePath)) {
            if (!(uploadFilePath.endsWith("/") || uploadFilePath.endsWith("\\"))) {
                uploadFilePath += "/";
            }
        } else {
            uploadFilePath = basePath + DEFAULT_UPLOAD_FILE_PATH;
        }
        this.uploadFilePath = uploadFilePath;
    }

    /**
     * 
     * StaticResult 静态化操作结果封装类
     *
     */
    public class StaticResult {
        private boolean result;
        private String filePath;

        public StaticResult() {
        }

        public StaticResult(boolean result, String filePath) {
            this.result = result;
            this.filePath = filePath;
        }

        public boolean getResult() {
            return result;
        }

        public String getFilePath() {
            return filePath;
        }
    }

    /**
     * 
     * FileInfo 文件信息封装类
     *
     */
    public class FileInfo {
        private String fileName;
        private String path;
        private String alias;
        private boolean directory;
        private Date lastModifiedTime;
        private Date lastAccessTime;
        private Date creationTime;
        private long size;

        public FileInfo(String fileName, String path, String alias, boolean directory) {
            this.fileName = fileName;
            this.path = path;
            this.alias = alias;
            this.directory = directory;
        }

        public FileInfo(String fileName, String path, String alias, boolean directory, BasicFileAttributes attrs) {
            this.fileName = fileName;
            this.path = path;
            this.alias = alias;
            this.directory = directory;
            this.lastModifiedTime = new Date(attrs.lastModifiedTime().toMillis());
            this.lastAccessTime = new Date(attrs.lastAccessTime().toMillis());
            this.creationTime = new Date(attrs.creationTime().toMillis());
            this.size = attrs.size();
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public Date getLastModifiedTime() {
            return lastModifiedTime;
        }

        public void setLastModifiedTime(Date lastModifiedTime) {
            this.lastModifiedTime = lastModifiedTime;
        }

        public Date getLastAccessTime() {
            return lastAccessTime;
        }

        public void setLastAccessTime(Date lastAccessTime) {
            this.lastAccessTime = lastAccessTime;
        }

        public Date getCreationTime() {
            return creationTime;
        }

        public void setCreationTime(Date creationTime) {
            this.creationTime = creationTime;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public boolean isDirectory() {
            return directory;
        }

        public void setDirectory(boolean directory) {
            this.directory = directory;
        }
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String getCmsPath() {
        return cmsPath;
    }

    public String getSitePath() {
        return sitePath;
    }

    public void setSitePath(String sitePath) {
        this.sitePath = sitePath;
    }

    public void setCmsPath(String cmsPath) {
        this.cmsPath = cmsPath;
    }
}
