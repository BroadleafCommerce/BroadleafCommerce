### Adding exports to a new domain
1. Add the export module as a dependency in your POM
2. Create a new class extending ExportEntityType and add a new type in accordance to the domain that you're adding exports to
3. Create an admin extension handler to add a button for exporting on the page that you want exporting to be done in the admin
    - Most likely this would be done by creating a class that extends AbstractAdminAbstractControllerExtensionHandler and implements addAdditionalMainActions. Be sure that one of the button classes is `export-standard-action` in order for the JavaScript in the export module to automatically setup the onclick listener for it.
    
    ```
    @Override
    public ExtensionResultStatusType addAdditionalMainActions(String sectionClassName, List<EntityFormAction> actions) {
        
        if (sectionClassName.equals(CustomerSegment.class.getName()) || sectionClassName.equals(CustomerSegmentImpl.class.getName())) {
            actions.add(new EntityFormAction("export-customer-segment")
                    .withDisplayText("export_customer_segments")
                    .withIconClass("icon-upload")
                    .withButtonClass("export-standard-action")
                    .withUrlOverride("/export/customer-segment")
            );
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }
        
        return ExtensionResultStatusType.NOT_HANDLED;
    }
    ```
    
4. Create a controller that has a GET and POST for whichever url you chose for the `.withUrlOverride` property. In order to use the default modal popup provided in the export module be sure that the GET returns `"views/configureExportPrompt"`. An example of the GET and POST created for the CustomerSegment export is shown below

    ```
     /**
     * Shows the form that the allows the user to create an export file
     */
    @RequestMapping(value = "/customer-segment", method = RequestMethod.GET)
    public String viewConfigurePrompt(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        model.addAttribute("exportFormats", SupportedExportType.getTypes());
        model.addAttribute("exportEncodings", SupportedExportEncoding.getTypes());
        model.addAttribute("baseUrl", request.getRequestURL().toString());
        return "views/configureExportPrompt";
    }

    /**
     * Used by the Export Prompt to schedule a job so that the export can be processed later.
     */
    @RequestMapping(value = "/customer-segment", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Map<String, String> uploadExport(HttpServletRequest request,
                         @RequestParam(value = "shareable", defaultValue = "false") Boolean shareable,
                         @RequestParam("exportEncoding") String exportEncoding,
                         @RequestParam("exportType") String exportType) {
        Map<String, String> resp = new HashMap<>();
        customerSegmentExportScheduler.scheduleExport(exportType, exportEncoding, shareable);
        resp.put("status", "success");
        return resp;
    }
    ```
    
5. Next create an export event scheduler that extends AbstractExportEventScheduler. For example
    
    ```
    @Component("blCustomerSegmentExportEventScheduler")
    public class CustomerSegmentExportEventSchedulerImpl extends AbstractExportEventScheduler implements CustomerSegmentExportEventScheduler {
    
        @Resource(name = "blSystemEventSender")
        protected SystemEventSender systemEventSender;
        
        @Override
        public void scheduleExport(String formatType, String encodingType, boolean shareable) {
            SystemEvent event = createSystemEvent(CustomerSegmentExportEventConsumer.EVENT_TYPE);
            createEventContext(event, formatType, encodingType, shareable);
            systemEventSender.sendEvents(Arrays.asList(event));
        }
        
    }
    ```
    
6. Then create an export event consumer that extends AbstractExportEventConsumer. For exmple

    ```
    @Component("blCustomerSegmentExportEventConsumer")
    public class CustomerSegmentExportEventConsumer extends AbstractExportEventConsumer {
        
        @Resource(name = "blCustomerSegmentExportService")
        protected CustomerSegmentExportService customerSegmentExportService;
        
        public static final String CUST_SEG_EXP_FILENAME = "customerSegmentExport.csv";
        
        public static final String EVENT_TYPE = "CUSTOMER_SEG_EXPORT";
        
        private Integer batchSize = null;
        
        @Override
        protected void export(ExportEventConsumerContext context) throws IOException {
            boolean isFirst = true;
            int start = 0;
            while (customerSegmentExportService.exportBatchCustomerSegmentsCsv(start, getBatchSize(), context.getOutputStream(), isFirst)) {
                start += getBatchSize();
                isFirst = false;
            }
        }
    
        @Override
        public String getEventType() {
            return EVENT_TYPE;
        }
        
        @Override
        public String getExportFileName() {
            return CUST_SEG_EXP_FILENAME;
        }
    
        @Override
        public String getEntityType() {
            return CustomerSegmentExportEntityType.CUSTOMER_SEGMENT.getType();
        }
        
        @Override
        public String getExportFriendlyName() {
            return "Customer Segment Export";
        }
        
        protected int getBatchSize() {
            if (batchSize == null) {
                batchSize = BLCSystemProperty.resolveIntSystemProperty("customerSegments.export.batchSize", 100); 
            }
            return batchSize;
        }
    
    }
    ```
    
7. Lastly create a service that actually does the work of exporting which will be called by `YouEventConsumer.export` method. For example

    ```
    @Service("blCustomerSegmentExportService")
    public class CustomerSegmentExportServiceImpl implements CustomerSegmentExportService {
        
        @Resource(name = "blCustomerSegmentService")
        protected CustomerSegmentService customerSegmentService;
        
        @Override
        public void exportCustomerSegmentsCsv(OutputStream output, int batchSize) throws IOException {
            boolean isFirst = true;
            int start = 0;
            while (exportBatchCustomerSegmentsCsv(start, batchSize, output, isFirst)) {
                start += batchSize;
                isFirst = false;
            }
        }
        
        @Override 
        public boolean exportBatchCustomerSegmentsCsv(int start, int count, OutputStream output, boolean useHeaders) throws IOException {
            CsvMapper mapper = new CsvMapper();
            // This has to be configured to false so that the output stream isn't closed after writing the first batch to it
            mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
            CsvSchema schema = mapper.schemaFor(CustomerSegmentExportDTO.class);
            ObjectWriter writer = mapper.writer(schema.withUseHeader(useHeaders).withoutQuoteChar());
            return exportBatchCustomerSegments(start, count, writer, output);
        }
        
        protected boolean exportBatchCustomerSegments(int start, int count, ObjectWriter writer, OutputStream output) throws JsonGenerationException, JsonMappingException, IOException {
            List<CustomerCustomerSegmentXref> xrefs = customerSegmentService.findDetachedBatchCustomerCustomerSegXrefs(start, count);
            List<CustomerSegmentExportDTO> currentBatch = new ArrayList<>();
            for (CustomerCustomerSegmentXref custXref : xrefs) {
                CustomerSegmentExportDTO dto = new CustomerSegmentExportDTO();
                dto.wrapDetails(custXref.getCustomerSegment(), custXref.getCustomer().getEmailAddress());
                currentBatch.add(dto);
            }
            customerSegmentService.clear();
            writer.writeValue(output, currentBatch);
            return CollectionUtils.size(xrefs) == count;
        }
    }
    ```

NOTE: If you would wish for your export to support additional formats or encodings then it's advised to create classes extending `SupportedExportEncoding` and/or `SupportedExportType`