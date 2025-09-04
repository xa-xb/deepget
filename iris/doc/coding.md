#### 禁止使用的旧类

| 旧类	                | 替代类型                           | 	原因 / 适用场景                    |
|--------------------|--------------------------------|-------------------------------|
|java.io.File|java.nio.file.Path 和 Files 工具类| java.io.File 的 API 不直观，错误处理复杂 |
| java.sql.Date      | 	LocalDate	                    | 表示只有日期的信息。                    |
| java.sql.Timestamp | 	Instant 或 LocalDateTime       | 	时间戳处理。                       |
| java.util.Date     | LocalDateTime 或 ZonedDateTime	 | 表示日期和时间（带或不带时区）。              |
