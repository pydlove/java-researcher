@Service
public class MetadataScanner {
    @Autowired
    private LicenseService licenseService;
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    public void scanAndProcessMetadata(Metadata metadata) {
        // 1. 尝试获取授权
        if (!licenseService.tryAcquireLicense(1)) {
            throw new RuntimeException("授权数不足，无法扫描");
        }
        
        try {
            // 2. 扫描元数据
            String metadataJson = convertToJson(metadata);
            
            // 3. 发送到Kafka
            kafkaTemplate.send("metadata_topic", metadataJson)
                .addCallback(
                    success -> licenseService.confirmLicenseUsage(1),
                    failure -> licenseService.rollbackLicense(1)
                );
        } catch (Exception e) {
            // 发生异常时回滚授权
            licenseService.rollbackLicense(1);
            throw e;
        }
    }
}