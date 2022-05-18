package com.my.azusato.unit.page;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.util.ReflectionTestUtils;

import com.my.azusato.page.MyPageRequest;
import com.my.azusato.page.MyPageResponse;
import com.my.azusato.page.MyPaging;

public class MyPagingTest{
	

	@Nested
	class Of{
		
		@ParameterizedTest
		@MethodSource("com.my.azusato.unit.page.MyPagingTest#of")
		public void givenNoraml_returnNormal(MyPageRequest pageReq,MyPageResponse expect ) throws Exception {
			MyPageResponse response = MyPaging.of(pageReq);
			
			Assertions.assertEquals(expect,response);
		}
		
		@ParameterizedTest
		@MethodSource("com.my.azusato.unit.page.MyPagingTest#ofError")
		public void givenAbNoraml_returnError(MyPageRequest pageReq,IllegalArgumentException expect) {
			IllegalArgumentException result = Assertions.assertThrowsExactly(IllegalArgumentException.class, ()->{
				MyPaging.of(pageReq);
			});
			
			Assertions.assertEquals(expect.getMessage(), result.getMessage());
		}
	}
	
	@Nested
	class GetPages{
		
		@SuppressWarnings("unchecked")
		@ParameterizedTest
		@MethodSource("com.my.azusato.unit.page.MyPagingTest#getPages")
		public void givenNoraml_returnNormal(int currentPageNo, int displayPageCount, int totalPage, List<Integer> expect) {
			List<Integer> result = (List<Integer>)ReflectionTestUtils.invokeMethod(MyPaging.class,"getPages", currentPageNo, displayPageCount, totalPage);
			Assertions.assertEquals(expect, result);
		}
	}
	
	@SuppressWarnings("unused")
	private static Stream<Arguments> of() {
		int pageOfElement = 5;
		int pagesOfpage = 3;
		int dataSize = (pageOfElement * 7) + 1; // 最大8ページ
		int totalPage = 8;
		
		return Stream.of(
					Arguments.of(
							MyPageRequest.builder()
								.currentPageNo(1)
								.pageOfElement(pageOfElement)
								.pagesOfpage(pagesOfpage)
								.totalElements(dataSize)
								.build(),
							MyPageResponse.builder()
								.pages(List.of(1,2,3))
								.hasNext(true)
								.hasPrivious(false)
								.currentPageNo(1)
								.totalPage(totalPage)
								.build()
					),
					Arguments.of(
							MyPageRequest.builder()
								.currentPageNo(2)
								.pageOfElement(pageOfElement)
								.pagesOfpage(pagesOfpage)
								.totalElements(dataSize)
								.build(),
							MyPageResponse.builder()
								.pages(List.of(1,2,3))
								.hasNext(true)
								.hasPrivious(false)
								.currentPageNo(2)
								.totalPage(totalPage)
								.build()
					),
					Arguments.of(
							MyPageRequest.builder()
								.currentPageNo(3)
								.pageOfElement(pageOfElement)
								.pagesOfpage(pagesOfpage)
								.totalElements(dataSize)
								.build(),
							MyPageResponse.builder()
								.pages(List.of(1,2,3))
								.hasNext(true)
								.hasPrivious(false)
								.currentPageNo(3)
								.totalPage(totalPage)
								.build()
					),
					Arguments.of(
							MyPageRequest.builder()
								.currentPageNo(4)
								.pageOfElement(pageOfElement)
								.pagesOfpage(pagesOfpage)
								.totalElements(dataSize)
								.build(),
							MyPageResponse.builder()
								.pages(List.of(4,5,6))
								.hasNext(true)
								.hasPrivious(true)
								.currentPageNo(4)
								.totalPage(totalPage)
								.build()
					),
					Arguments.of(
							MyPageRequest.builder()
								.currentPageNo(5)
								.pageOfElement(pageOfElement)
								.pagesOfpage(pagesOfpage)
								.totalElements(dataSize)
								.build(),
							MyPageResponse.builder()
								.pages(List.of(4,5,6))
								.hasNext(true)
								.hasPrivious(true)
								.currentPageNo(5)
								.totalPage(totalPage)
								.build()
					),
					Arguments.of(
							MyPageRequest.builder()
								.currentPageNo(6)
								.pageOfElement(pageOfElement)
								.pagesOfpage(pagesOfpage)
								.totalElements(dataSize)
								.build(),
							MyPageResponse.builder()
								.pages(List.of(4,5,6))
								.hasNext(true)
								.hasPrivious(true)
								.currentPageNo(6)
								.totalPage(totalPage)
								.build()
					),
					Arguments.of(
							MyPageRequest.builder()
								.currentPageNo(7)
								.pageOfElement(pageOfElement)
								.pagesOfpage(pagesOfpage)
								.totalElements(dataSize)
								.build(),
							MyPageResponse.builder()
								.pages(List.of(7,8))
								.hasNext(false)
								.hasPrivious(true)
								.currentPageNo(7)
								.totalPage(totalPage)
								.build()
					),
					Arguments.of(
							MyPageRequest.builder()
								.currentPageNo(8)
								.pageOfElement(pageOfElement)
								.pagesOfpage(pagesOfpage)
								.totalElements(dataSize)
								.build(),
							MyPageResponse.builder()
								.pages(List.of(7,8))
								.hasNext(false)
								.hasPrivious(true)
								.currentPageNo(8)
								.totalPage(totalPage)
								.build()
					)
				);
	}
	
	@SuppressWarnings("unused")
	private static Stream<Arguments> getPages() {
		return Stream.of(
				Arguments.of(1,1,1,List.of(1)),
				Arguments.of(1,3,8,List.of(1,2,3)),
				Arguments.of(2,3,8,List.of(1,2,3)),
				Arguments.of(3,3,8,List.of(1,2,3)),
				Arguments.of(4,3,8,List.of(4,5,6)),
				Arguments.of(5,3,8,List.of(4,5,6)),
				Arguments.of(6,3,8,List.of(4,5,6)),
				Arguments.of(7,3,8,List.of(7,8)),
				Arguments.of(8,3,8,List.of(7,8))
			);
	}
	
	@SuppressWarnings("unused")
	private static Stream<Arguments> ofError() {
		return Stream.of(
					Arguments.of(
							MyPageRequest.builder()
								.currentPageNo(0)
								.pageOfElement(1)
								.pagesOfpage(1)
								.build()
							,new IllegalArgumentException("現在ページ番号は0以外になれません。")),
					Arguments.of(
							MyPageRequest.builder()
								.currentPageNo(1)
								.pageOfElement(1)
								.pagesOfpage(0)
								.build()
							,new IllegalArgumentException("ページリストは0以外になれません。")),
					Arguments.of(
							MyPageRequest.builder()
								.currentPageNo(1)
								.pageOfElement(0)
								.pagesOfpage(1)
								.build()
							,new IllegalArgumentException("ページのElement表示数は0以外になれません。")),
					Arguments.of(
							MyPageRequest.builder()
								.currentPageNo(1)
								.pageOfElement(1)
								.pagesOfpage(1)
								.totalElements(-1)
								.build()
							,new IllegalArgumentException("総Element数はマイナスになれません。"))
				);
	}
}
