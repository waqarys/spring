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