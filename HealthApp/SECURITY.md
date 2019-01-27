# Authentication Architecture
- SecurityContextHolder
- SecurityContext
- Authentication
```
Object principal = SecurityContextHolder.getContext().  
        getAuthentication().getPrincipal();
```
- UserDetails
- UserDetailsService
```aidl
UserDetails loadUserByUsername(String username) throws   
        UsernameNotFoundException;
```
- GrantedAuthority : Usually loaded by UserDetailsService, GrantedAuthority is related to the setting application-wide permissions granted to the principal in the form of roles, such as ROLE_ADMINISTRATOR, ROLE_USER, and  so on.

# Authorization Architecture
- AccessDecisionManager

# Setting up spring-security
```aidl
<dependencies>
  <!-- ... other dependency elements ... -->
  <dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-web</artifactId>
    <version>4.2.3.RELEASE</version>
  </dependency>
  <dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-config</artifactId>
    <version>4.2.3.RELEASE</version>
  </dependency>
</dependencies>
```

# Logout 
```aidl
@GetMapping(value="/logout")
public ExecutionStatus logout (HttpServletRequest request, HttpServletResponse response) {
  Authentication auth = SecurityContextHolder.getContext().getAuthentication();
  if (auth != null){
    new SecurityContextLogoutHandler().logout(request, response, auth);
  }
  return new ExecutionStatus("USER_LOGOUT_SUCCESSFUL", "User is logged out");
}
```

# OAuth2 Implementation for Spring Web App
1. Cors Filter:  First and foremost, it will be required to configure CORS filter to allow Authorization to be passed in header. 
```aidl
@Component
        @Order(Ordered.HIGHEST_PRECEDENCE)
        public class CorsFilter implements Filter {
        
            @Override
            public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
            final HttpServletResponse response = (HttpServletResponse) res;
            
            response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
            
            }
}
```

2. Resource Server : resource server provides an access to the protected resource.
```aidl
@Configuration
        @EnableResourceServer
        public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

            @Autowired 
            private TokenStore tokenStore;

            @Override
            public void configure(HttpSecurity http) throws Exception {
        http.requestMatchers().antMatchers("/doctor/**", "/rx/**", "/account/**")
         .and()
         .authorizeRequests()
         .antMatchers (HttpMethod.GET,"/doctor/**").access("#oauth2.hasScope('doctor') and #oauth2.hasScope('read')")
         .antMatchers (HttpMethod.POST,"/doctor/**").access("#oauth2.hasScope('doctor') and #oauth2.hasScope('write')")
         .antMatchers (HttpMethod.GET,"/rx/**").access("#oauth2.hasScope('doctor') and #oauth2.hasScope('read')")
         .antMatchers (HttpMethod.POST,"/rx/**").access("#oauth2.hasScope('doctor') and #oauth2.hasScope('write')")
         .antMatchers("/account/**").permitAll()
         .and()
         .exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler())
         .and()
         .csrf().disable();
            }

            @Override    
            public void configure(final ResourceServerSecurityConfigurer config) {    
            final DefaultTokenServices defaultTokenServices = new DefaultTokenServices(); 
            defaultTokenServices.setTokenStore(this.tokenStore);
            config.tokenServices(defaultTokenServices);    
            }
        }
```

- it will also be required to enable method security using GlobalMethodSecurityConfiguration and @EnableGlobalMethodSecurity annotation.
```aidl
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class GlobalMethodSecurityConfig extends GlobalMethodSecurityConfiguration {

  @Override
  protected MethodSecurityExpressionHandler createExpressionHandler() {
    return new OAuth2MethodSecurityExpressionHandler();
  }
}
```

3. Authorization Server : 
- It is the authorization server that provides access token to the users based on the valid user credentials, which are, then, used to access the protected resources on the resource server
- The @EnableAuthorizationServer annotation is a convenience annotation used for enabling an authorization server whose key building blocks are AuthorizationEndpoint and TokenEndpoint. 
```aidl
@Configuration
        @EnableAuthorizationServer
        public class AuthServerOAuth2Config extends AuthorizationServerConfigurerAdapter {

          @Autowired
          @Qualifier("authenticationManagerBean")
          private AuthenticationManager authenticationManager;

          @Autowired
          private DataSource dataSource;

          @Override
          public void configure(final AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
            oauthServer
              .tokenKeyAccess("permitAll()")
              .checkTokenAccess("isAuthenticated()");
          }

          @Override
          public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
            clients.jdbc(this.dataSource);
          }

          @Override
          public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
         tokenEnhancerChain.setTokenEnhancers (Arrays.asList(tokenEnhancer()));
            endpoints.tokenStore(tokenStore())           .tokenEnhancer(tokenEnhancerChain).authenticationManager (authenticationManager);
          }

          @Bean
          @Primary
          public DefaultTokenServices tokenServices() {
            final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
            defaultTokenServices.setTokenStore(tokenStore());
            defaultTokenServices.setSupportRefreshToken(true);
            return defaultTokenServices;
          }

          @Bean
          public TokenEnhancer tokenEnhancer() {
            return new CustomTokenEnhancer();
          }

          @Bean(name="tokenStore")
          public TokenStore tokenStore() {
            return new JdbcTokenStore(this.dataSource);
          }
        }
```

4. Token Enhancer : The TokenEnhancer is called after the access and refresh tokens have been generated but before they are stored by an AuhorizationServerTokenServices implementation
```aidl
public class CustomTokenEnhancer implements TokenEnhancer {
            @Override
            public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
                final Map<String, Object> additionalInfo = new HashMap<>();
                Collection<GrantedAuthority> authorities = authentication.getAuthorities();
                Object[] ga = authorities.toArray();
                SimpleGrantedAuthority sga = (SimpleGrantedAuthority) ga[0];
                String role = sga.getAuthority();
                additionalInfo.put("role", role);
                ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
                return accessToken;
            }
        }
```