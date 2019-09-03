# Excel Object Mapper

`EOM(Excel Object Mapper)`는 Excel 데이터를 미리 정의한 `@ExcelObject` & `@ExcelColumn` 값에 따라 매핑하는 라이브러리입니다.



### @ExcelObject

- excelColumn 옵션

```java
@Excelobject(excelColumn=ExcelColumn.class)
```



### @ExcelColumn 

```java
@ExcelColumn
```



### ExcelDocumentBuilder

> constructor() 

```java

```

> build()

```java

```



### ExcelObjectBuilder



### Exception

#### EOMWrongExcelObjectException

- Compile Exception
  - 설정한 ExcelObject가 ExcelObject Interface를 구현하지 않았을 때 (Compile Exception)
- Checked Exception
  - 설정한 ExcelObject가 ExcelObject Interface를 구현했으나 구현클래스가 아닐 때 (InstantiationException)
  - 설정한 ExcelObject의 생성자 접근 범위가 public이 아닐 때 (IllegalAccessException)



#### EOMWrongExcelColumnException

- Compile Exception
  - 설정한 ExcelColumn이 ExcelColumn Interface를 구현하지 않았을 때 ( Compile Exception )
  - 설정한 ExcelColumn에 매핑된 ExcelObject가 없을 때 ( Compile Exception )
    - EOMNotMatchColumnAndObjectException
  - 설정한 ExcelColumn에 필요한 항목이 없을때 ( order, column, type )
    - EOMNotFoundExcelColumnFieldException
  - 설정한 ExcelColumn에 매핑된 ExcelObject에 Column이 ExcelColumn의 column 값과 다를 때
    - EOMNotFoundFieldException



### RuntimeException

#### EOMNotFoundFieldException

- Runtime Exception 
  - builder 에서 미리 검증되어 이론 상 Runtime 시에 발생하지 않음



#### EOMWrongExcelDataTypeException (Runtime)

> `ExcelDocument(ExcelColumn)`의 데이터가 `ExcelObject`의 타입과 같지 않습니다.
>
> Document -> Object

- ExcelColumn --> Integer && ExcelObject --> Integer (두 부분이 다른 것은 EOMNotMatchColumnAndObjectException)
- ExcelDocument -> "abc"



#### ~~EOMWrongObjectDataTypeException~~

> `ExcelObject`의 데이터가 `ExcelDocument(ExcelColumn)` 의 타입과 같지 않습니다.
>
> Object -> Document

- Excel



#### EOMWrongHeaderException

- Rumtime Exception



## TODO

- [ ] EOM Dependency 주입하고 프로젝트 실행시에 Annotation Processing 실행하도록 처리
- [ ] Lombok 및 다른 Annotation Processing Dependency와 충돌나지 않도록 처리