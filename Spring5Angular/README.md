# RequestParam Annotation
```aidl
 @Controller
    @RequestMapping("/account/*")
    public class UserAccountController {
 
        @GetMapping("/signup")
        public String signup() {
            return "signup";
        } 

        @PostMapping("/signup/process")
        public String processSignup(ModelMap model, 
        @RequestParam("nickname") String nickname, 
        @RequestParam("emailaddress") String emailAddress, 
        @RequestParam("password") String password) {
            model.addAttribute("login", true);
            model.addAttribute("nickname", nickname);
            return "index";
        }
    }

```

# RequestBody Annotation
```aidl
@RestController
    public class RestDemoController {
 
        @PostMapping("/hello")
        public HelloMessage getHelloMessage(@RequestBody User user) {
            HelloMessage helloMessage = new HelloMessage();
            String name = user.getName();
            helloMessage.setMessage( "Hello " + name + "! How are you doing?");
            helloMessage.setName(name);
            return helloMessage;
        }  
    }
```

# Path Variable Annotation
```aidl
@Controller
    @SpringBootApplication
    public class HealthAppApplication {
 
      @RequestMapping("/") 
      public String home() {
        return "index";
      }
 
      @GetMapping("/{nickname}") 
      public String home(ModelMap model, @PathVariable String nickname) {
        model.addAttribute("name", nickname);
        return "index";
      }
 
      public static void main(String[] args) {
        SpringApplication.run(HealthAppApplication.class, args);
      }
    }
```

# Interceptors
- `HandlerInterceptor` interface has the following methods
1. preHandle: The code within the preHandle method gets executed before the controller method is invoked
2. postHandle: The code within the postHandle method is executed after the controller method is invoked
3. afterCompletion: The code within afterCompletion is executed after the view gets rendered 

```aidl
public class SignupInterceptor extends HandlerInterceptorAdapter {

        @Override
        public boolean preHandle(HttpServletRequest request, 
               HttpServletResponse response, Object handler) throws Exception {
 
          String emailAddress = request.getParameter("emailaddress");
          String password = request.getParameter("password");
 
          if(StringUtils.isEmpty(emailAddress) ||             
          StringUtils.containsWhitespace(emailAddress) ||
          StringUtils.isEmpty(password) || 
          StringUtils.containsWhitespace(password)) {
            throw new Exception("Invalid Email Address or Password. 
                                 Please try again.");
          }
 
          return true;
        }

        @Override
        public void afterCompletion(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    Object handler, Exception exception)
        throws Exception {
          ... 
        }

        @Override
        public void postHandle(HttpServletRequest request, 
                    HttpServletResponse response, 
                    Object handler, ModelAndView modelAndView)
        throws Exception {
          ... 
        }
      }
```

- Implement the configuration class
```aidl
@Configuration 
public class AppConfig extends WebMvcConfigurerAdapter { 
 
 @Override
 public void addInterceptors(InterceptorRegistry registry) {
 registry.addInterceptor(new SignupInterceptor()).addPathPatterns("/account/signup/process");
 }
}
```

- Controller for the interceptor
```aidl
@Controller
@RequestMapping("/account/*")
public class UserAccountController {
 
 @RequestMapping("/signup")
 public String signup() {
 return "signup";
 } 

 @RequestMapping("/signup/process")
 public String processSignup(ModelMap model, @RequestParam("nickname") String nickname,
 @RequestParam("emailaddress") String emailAddress, @RequestParam("password") String password) {
  model.addAttribute("login", true);
  model.addAttribute("nickname", nickname);
  return "index";
 }
 
}
```

# Handling Response
Responses from the controller
- `ModelAndView`
- `@ResponseBody`

**Response as an instance of ModelAndView**
- Model : Map object to store key-value pair
```aidl
 @Controller
    @RequestMapping("/account/*")
    public class UserAccountController {
 
      @PostMapping("/signup/process")
      public ModelAndView processSignup(ModelMap model, @RequestParam("nickname")       String  nickname, @RequestParam("emailaddress") 
      String emailAddress, @RequestParam("password") String password) {
        model.addAttribute("login", true);
        model.addAttribute("nickname", nickname);
        model.addAttribute("message", "Have a great day ahead.");
        return new ModelAndView("index", model);
      }
    }

```

**@ResponseBody Annotation**
- `@ResponseBody` can be applied both at class level and the method level.
- Class level : @RestController = @Controller + @ResponseBody
- When the value returned is an object, the object is converted into an appropriate JSON or XML format by HttpMessageConverters. The format is decided based on the value of  the produce attribute of the @RequestMapping annotation, and also the type of content that the client accepts.
```aidl
@Controller
    public class RestDemoController {
 
      @RequestMapping(value="/hello", method=RequestMethod.POST, produces="application/json")
      @ResponseBody
      public HelloMessage getHelloMessage(@RequestBody User user) {
        HelloMessage helloMessage = new HelloMessage();
        String name = user.getName();
        helloMessage.setMessage( "Hello " + name + "! How are you doing?");
        helloMessage.setName(name);
        return helloMessage;
      }
    }
``` 

# Restful Web Service
e.g., 
```aidl
@RestController
    public class DoctorSearchController {
 
      @Autowired
      DoctorService docService;
 
      @RequestMapping(value="/doctors", method=RequestMethod.GET, 
                      produces="application/json")
      public DoctorList searchDoctor(
             @RequestParam(value="location", required=false) String location,
             @RequestParam(value="speciality", required=false) String speciality) 
      {
          DoctorList docList = docService.find(location, speciality);
        return docList;
      }
    }
```

- Client Response
```aidl
[{
     "id": "doc1",
     "firstName": "Calvin",
     "lastName": "Hobbes",
     "specialityCode": "pediatrics"
    },
    {
      "id": "doc2",
      "firstName": "Susan",
      "lastName": "Storm",
      "specialityCode": "cardiology"
    }]
```