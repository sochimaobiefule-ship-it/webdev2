# WEBDEV2 — Week 9 Lab Answers

Name: OBIEFULE DECLAN
Student ID: 2510223
GitHub repo: https://github.com/sochimaobiefule-ship-it/webdev2
Date submitted: 15/10/2026

---

## Task 1 — One-to-One + One-to-Many Mappings

**Approach / explanation:**
For the 1:1 relationship between `User` and `UserProfile`, I set `User` as the owning side using `@JoinColumn(name = "profile_id")` while `UserProfile` acts as the inverse side with `mappedBy = "profile"`. For the 1:N relationship, `Employee` owns the foreign key via `@ManyToOne` with `@JoinColumn(name = "department_id", nullable = false)` and `fetch = FetchType.LAZY`. `Department` owns the inverse side with `@OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)`. I also implemented synchronization helper methods `addEmployee()` and `removeEmployee()` inside `Department` to keep both ends of the memory relationship aligned.

**Code / endpoint(s) used (if applicable):**
```java
// Department.java
@Entity
@Table(name = "departments")
public class Department {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Employee> employees = new ArrayList<>();

    public void addEmployee(Employee employee) {
        employees.add(employee);
        employee.setDepartment(this);
    }

    public void removeEmployee(Employee employee) {
        employees.remove(employee);
        employee.setDepartment(null);
    }
}

// Employee.java
@Entity
@Table(name = "employees")
public class Employee {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
}
```

**Evidence (screenshot filename, request/response sample, or file reference in this folder):**
Hibernate: 
    create table departments (
        id bigint not null auto_increment,
        name varchar(255) not null,
        primary key (id)
    ) engine=InnoDB

Hibernate: 
    create table employees (
        id bigint not null auto_increment,
        name varchar(255) not null,
        department_id bigint not null,
        primary key (id)
    ) engine=InnoDB

Hibernate: 
    insert 
    into
        departments
        (name) 
    values
        (?)

Hibernate: 
    insert 
    into
        employees
        (department_id, name) 
    values
        (?, ?)

Hibernate: 
    insert 
    into
        employees
        (department_id, name) 
    values
        (?, ?)

**Reflection question:**
_Which entity owns the `Department`–`Employee` relationship and why, and what value must `mappedBy` contain?_

Your answer:
Employee owns the relationship because it holds the physical foreign key column (department_id) in the database table. The mappedBy property in Department must be set to "department", which corresponds directly to the field name pointing back to Department inside the Employee entity class.

---

## Task 2 — Many-to-Many Join Table + Association Entity

**Approach / explanation:**
Instead of a basic direct `@ManyToMany` join table, I modeled the relationship using `Enrollment` as an association entity. This allows tracking additional payload attributes such as `enrollmentDate` and `grade`. `Student` and `Course` each have a `@OneToMany` relationship targeting `Enrollment`, while `Enrollment` holds `@ManyToOne` foreign keys to both. To prevent a student from enrolling twice in the exact same course, I declared a composite unique constraint `@UniqueConstraint(name = "uk_student_course", columnNames = {"student_id", "course_id"})` on the `enrollments` table.

**Code / endpoint(s) used (if applicable):**
```java
// Enrollment.java
@Entity
@Table(name = "enrollments", uniqueConstraints = {
    @UniqueConstraint(name = "uk_student_course", columnNames = {"student_id", "course_id"})
})
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate enrollmentDate;
    private String grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    public Enrollment() {}

    public Enrollment(Student student, Course course, String grade) {
        this.student = student;
        this.course = course;
        this.grade = grade;
        this.enrollmentDate = LocalDate.now();
    }

    public Long getId() { 
        return id; 
    }public LocalDate getEnrollmentDate() { 
        return enrollmentDate; 
    }public String getGrade() { 
        return grade; 
    }public Student getStudent() { 
        return student; 
    }public Course getCourse() { 
        return course; 
    }

    public void setId(Long id) { 
        this.id = id; 
    }public void setEnrollmentDate(LocalDate enrollmentDate) { 
        this.enrollmentDate = enrollmentDate; 
    }public void setGrade(String grade) { 
        this.grade = grade; 
    }public void setStudent(Student student) { 
        this.student = student; 
    }public void setCourse(Course course) { 
        this.course = course; 
    }
}
```

**Evidence (screenshot filename, request/response sample, or file reference in this folder):**
Hibernate: 
    alter table enrollments 
       add constraint uk_student_course unique (student_id, course_id)

TASK 2 TEST RUNNER START
Hibernate: 
    insert 
    into
        students
        (name) 
    values
        (?)

Hibernate: 
    insert 
    into
        courses
        (title) 
    values
        (?)

Hibernate: 
    insert 
    into
        enrollments
        (course_id, enrollment_date, grade, student_id) 
    values
        (?, ?, ?, ?)

Hibernate: 
    insert 
    into
        enrollments
        (course_id, enrollment_date, grade, student_id) 
    values
        (?, ?, ?, ?)

2026-10-15 ERROR org.hibernate.engine.jdbc.spi.SqlExceptionHelper : Duplicate entry '1-1' for key 'enrollments.uk_student_course'
Caught duplicate enrollment constraint violation: could not execute statement [Duplicate entry '1-1' for key 'enrollments.uk_student_course']
TASK 2 TEST RUNNER END

**Reflection question:**
_When must a many-to-many relationship be modeled as an association entity instead of a direct `@ManyToMany`, and what did the unique constraint protect in your test?_

Your answer:
A many-to-many relationship must be modeled as an association entity whenever the link itself needs extra payload fields beyond just the two primary/foreign keys. In my test, the unique constraint uk_student_course protected the database against duplicate relationship records, ensuring a student cannot be enrolled in the exact same course more than once.

---

## Task 3 — Fetch Strategies Experiment — LAZY vs EAGER

**Approach / explanation:**
I configured `@ManyToOne` on `Employee.department` with `FetchType.LAZY`. In a non-transactional service method, reading `employee.getDepartment().getName()` threw a `LazyInitializationException` because the Hibernate session was closed before accessing the lazy proxy. I compared two fixes: (a) adding `@Transactional` to keep the persistence context open, which issued 2 separate `SELECT` queries, and (b) writing a repository query with JPQL `JOIN FETCH`, which fetched both `Employee` and `Department` in a single SQL `JOIN` query.

**Code / endpoint(s) used (if applicable):**
```java
// EmployeeRepository.java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Query("SELECT e FROM Employee e JOIN FETCH e.department WHERE e.id = :id")
    Optional<Employee> findByIdWithDepartment(@Param("id") Long id);
}

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public String getDepartmentNameLazyUntransactional(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow();
        return employee.getDepartment().getName();
    }

    @Transactional
    public String getDepartmentNameTransactional(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow();
        return employee.getDepartment().getName();
    }
    
    public String getDepartmentNameFetchJoin(Long employeeId) {
        Employee employee = employeeRepository.findByIdWithDepartment(employeeId).orElseThrow();
        return employee.getDepartment().getName();
    }
}
```

**Evidence (screenshot filename, request/response sample, or file reference in this folder):**
TASK 3 TEST RUNNER START
Step 1: Testing Non-Transactional Lazy Load
Hibernate: 
    select
        e1_0.id,
        e1_0.department_id,
        e1_0.name 
    from
        employees e1_0 
    where
        e1_0.id=?

Caught expected LazyInitializationException: org.hibernate.LazyInitializationException - could not initialize proxy [com.zilpher.webdev2.entity.Department#3]

Step 2: Testing @Transactional Fix
Hibernate: 
    select
        e1_0.id,
        e1_0.department_id,
        e1_0.name 
    from
        employees e1_0 
    where
        e1_0.id=?

Hibernate: 
    select
        d1_0.id,
        d1_0.name 
    from
        departments d1_0 
    where
        d1_0.id=?

Department name retrieved via @Transactional: Research & Development

Step 3: Testing JOIN FETCH Fix
Hibernate: 
    select
        e1_0.id,
        e1_0.department_id,
        d1_0.id,
        d1_0.name,
        e1_0.name 
    from
        employees e1_0 
    join
        departments d1_0 
            on d1_0.id=e1_0.department_id 
    where
        e1_0.id=?
Department name retrieved via JOIN FETCH: Research & Development
TASK 3 TEST RUNNER END

**Reflection question:**
_What causes `LazyInitializationException`, and why is prefer-LAZY plus explicit fetch join/`@EntityGraph` preferable to marking everything EAGER?_

Your answer:
LazyInitializationException is thrown when application code attempts to access an uninitialized lazy proxy after the underlying JPA session has already closed. Using FetchType.LAZY by default alongside explicit JOIN FETCH or @EntityGraph is preferred because it allows developers to customize SQL queries on a per-use-case basis, preventing unwanted table joins and memory bloat while avoiding the N+1 select problem.

---

## Task 4 — Cascade + orphanRemoval — Safe vs. Dangerous

**Approach / explanation:**
For the parent-child relationship, I configured `cascade = {CascadeType.PERSIST, CascadeType.MERGE}` and `orphanRemoval = true`. When an employee is removed from the department's list, Hibernate automatically issues a SQL `DELETE` query to purge the orphan row from the database. For shared references, I strictly avoided `CascadeType.REMOVE` and `orphanRemoval = true` to guarantee that deleting a student record does not accidentally delete shared courses that other students may be enrolled in.

**Code / endpoint(s) used (if applicable):**
```java
// Department.java
@OneToMany(mappedBy = "department", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
private List<Employee> employees = new ArrayList<>();

// Student.java
@OneToMany(mappedBy = "student", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
private List<Enrollment> enrollments = new ArrayList<>();
```

**Evidence (screenshot filename, request/response sample, or file reference in this folder):**
TASK 4 TEST RUNNER START
Step 1: Testing orphanRemoval on Department -> Employee
Hibernate: 
    insert 
    into
        departments
        (name) 
    values
        (?)

Hibernate: 
    insert 
    into
        employees
        (department_id, name) 
    values
        (?, ?)

Hibernate: 
    insert 
    into
        employees
        (department_id, name) 
    values
        (?, ?)

Hibernate: 
    delete 
    from
        employees 
    where
        id=?

Employee removed from collection and orphan deleted successfully

Step 2: Testing Deleting Student without Deleting Shared Course
Hibernate: 
    insert 
    into
        courses
        (title) 
    values
        (?)

Hibernate: 
    insert 
    into
        students
        (name) 
    values
        (?)

Hibernate: 
    delete 
    from
        students 
    where
        id=?

Hibernate: 
    select
        count(*) 
    from
        courses c1_0 
    where
        c1_0.id=?

Student deleted. Does shared Course still exist in DB? true
TASK 4 TEST RUNNER END

**Reflection question:**
_What is the difference between `CascadeType.REMOVE` and `orphanRemoval = true`, and in which situation is each appropriate or dangerous?_

Your answer:
CascadeType.REMOVE triggers a cascading delete when the parent entity itself is deleted. orphanRemoval = true goes further by deleting a child entity if it is disassociated/removed from the parent's collection, even if the parent entity remains alive in the database. orphanRemoval is appropriate for private, parent-child relationships, but dangerous on shared lookup entities, where disassociating or deleting one side should never wipe out shared system records.

---

## Task 5 — Capstone Milestone 2 — ERD to JPA Entity Model

**Approach / explanation:**
I translated my Capstone domain into JPA entity classes, mapping the cardinalities, foreign key columns, and owning sides. The domain includes a 1:1 relationship, a 1:N relationship, and an N:M relationship resolved via the `OrderItem` association entity between `CustomerOrder` and `Product`. All relationships default to `FetchType.LAZY` to ensure optimal performance, and cascade types are configured based on entity lifecycle dependencies.

**Code / endpoint(s) used (if applicable):**
```java
// CustomerOrder.java
@Entity
@Table(name = "customer_orders")
public class CustomerOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate orderDate;
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }
}

// OrderItem.java
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer quantity;
    private Double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private CustomerOrder order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
```

**Evidence (screenshot filename, request/response sample, or file reference in this folder):**
Hibernate: 
    create table customer_orders (
        id bigint not null auto_increment,
        order_date date,
        status varchar(255),
        user_id bigint,
        primary key (id)
    ) engine=InnoDB

Hibernate: 
    create table order_items (
        id bigint not null auto_increment,
        price float(53),
        quantity integer,
        order_id bigint not null,
        product_id bigint not null,
        primary key (id)
    ) engine=InnoDB

Hibernate: 
    create table products (
        id bigint not null auto_increment,
        name varchar(255) not null,
        price float(53),
        primary key (id)
    ) engine=InnoDB

TASK 5 CAPSTONE SMOKE TEST START
Hibernate: 
    insert 
    into
        user_profiles
        (bio, phone_number) 
    values
        (?, ?)

Hibernate: 
    insert 
    into
        users
        (profile_id, username) 
    values
        (?, ?)

Hibernate: 
    insert 
    into
        products
        (name, price) 
    values
        (?, ?)

Hibernate: 
    insert 
    into
        customer_orders
        (order_date, status, user_id) 
    values
        (?, ?, ?)

Hibernate: 
    insert 
    into
        order_items
        (order_id, price, product_id, quantity) 
    values
        (?, ?, ?, ?)    
Capstone object graph persisted and read back successfully
TASK 5 CAPSTONE SMOKE TEST END                    

**Reflection question:**
_For one relationship in your capstone model, state the cardinality, the owning side, where the FK/join table lives, and why you chose that fetch and cascade setting._

Your answer:
In the CustomerOrder -> OrderItem relationship, OrderItem is the owning side because it holds the order_id foreign key column in the order_items table. I selected FetchType.LAZY to prevent loading all order line items whenever basic order summary details are queried. I chose cascade = CascadeType.ALL and orphanRemoval = true because line items are privately owned components of a customer order—they should be created, updated, or removed automatically in tandem with their parent order.

---

## Self-Check

- [ ] All Week 9 work on the `week9` branch (project at branch root, no `week9/` folder)
- [ ] This file completed and renamed `answers.md`
- [ ] Repository link pasted into Moodle (no files uploaded)
