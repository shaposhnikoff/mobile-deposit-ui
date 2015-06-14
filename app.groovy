@Grab("thymeleaf-spring4")

@Controller
class JsApp {
	@Value('${api.proto}')
    private String apiProto;

	@Value('${api.host}')
    private String apiHost;

    @Value('${api.port}')
    private String apiPort;    

    @RequestMapping("/deposit")
    public String deposit(Model model){
    	model.addAttribute("apiProto", apiProto)
    	model.addAttribute("apiHost", apiHost)
    	model.addAttribute("apiPort", apiPort)
    	
    	return "deposit"
    }

}
