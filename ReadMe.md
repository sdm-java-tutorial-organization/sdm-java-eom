# Excel Object Mapper

`EOM(Excel Object Mapper)`는 Excel 데이터를 미리 정의한 `@ExcelObject` & `@ExcelColumn` 값에 따라 매핑하는 라이브러리입니다.



## @ExcelColumn 

시트의 Column을 정의하는 어노테이션입니다. 

- `name` : Column 노출 이름
- `index` : Column 순서
- `group` : Column 그룹
  - 그룹이란, Column의 동일한 항목을 묶어 노출하는 기능을 말합니다.

- `cellColor` : Cell 색상 (default : IndexedColors.WHITE)
- `borderStyle` : 테두리 스타일 (default : BorderStyle.THIN)
- `borderColor` : 테두리 색상 (default : IndexedColors.BLACK)

> 기본예제

```java
public class Inventory {
 
    @ExcelColumnt(name="NAME", index=0)
    public String name;
    
    @ExcelColumn(name="COUNT", index=1)
    public Integer count;
    
}
```



> 그룹예제

- `group`의 기본값을 0이며, 숫자가 커질 수록 상위 그룹을 의미합니다.
- `group`으로 데이터가 묶여 노출되기 때문에 의미가 다른 같은 이름의 group이 있어선 안됩니다.

```java
public class Inventory {
    
    @ExcelColumnt(name="CATEGORY", index=0, group=1)
    public String category;
    
    @ExcelColumnt(name="NAME", index=0)
    public String name;
    
    @ExcelColumn(name="COUNT", index=1)
    public Integer count;
    
}
```

![1567494120892](assets/1567494120892.png)





## @ExcelObject

다음 에너테이션은 `Class`에 설정가능합니다. 시트이름과 시트의 색상을 설정할 수 있습니다.

필수적이지 않으며 설정하지 않을 경우 기본값으로 설정됩니다.

- `name` : 시트의 이름을 설정합니다. (default : "default")
- `cellColor` : Cell 색상 (default : IndexedColors.YELLOW)
- `borderStyle` : 테두리 스타일 (default : BorderStyle.THIN)
- `borderColor` : 테두리 색상 (default : IndexedColors.BLACK)

```java
@Excelobject(name="default")
public class Inventory {
    
}
```



## ExcelObjectMapper

ExcelObjectMapper는 빌드를 실행하는 클래스입니다. 

Excel -> Object로 변환하는 `buildSheet`와 Object -> Excel로 변환하는 `buildObject` 두 가지의 메소드를 제공합니다.

내부적으로 `ColumnElement.class`라는 Iterator가 있기 때문에 반드시 정의된 object를 사용해야 하는 것이 아닌 

`List<ColumnElement>`로도 빌드가 가능합니다. (TODO : 제공예정)



> buildSheet (Excel -> Object)

```java
List<Inventory> items = ExcelObjectMapper.init()
    .initModel(Inventory.class)
    .initSheet(sheet)
    .buildSheet()
```



> buildObject (Object -> Excel)
>
> - initBook이 추가적으로 필요합니다. ( CellStyle 작업을 위함 )

```java
ExcelObjectMapper.init()
    .initModel(Inventory.class)
    .initBook(book)
    .initSheet(sheet)
    .buildObject(items)
```



## Exception

- 정리중