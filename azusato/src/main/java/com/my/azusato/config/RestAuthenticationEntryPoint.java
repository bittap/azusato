package com.my.azusato.config;

//@Component
//public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
//
//	private final HandlerExceptionResolver resolver;
//	
//	// // no qualifying bean of type 'org.springframework.web.servlet.HandlerExceptionResolver' available: expected single matching bean but found 2: errorAttributes,handlerExceptionResolver
//	// @RequiredArgsConstructorすると@Qualifierが効かない。
//	// 参考:https://stackoverflow.com/questions/42350828/why-qualifier-not-allowed-above-constructor
//	public RestAuthenticationEntryPoint(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
//		this.resolver = resolver;
//	}
//	
//	@Override
//	public void commence(HttpServletRequest request, HttpServletResponse response,
//			AuthenticationException authException) throws IOException, ServletException {
//		resolver.resolveException(request, response, null, authException);
//		
//	}
//}
