package seg3x02.employeeGql.resolvers

import java.util.*
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import seg3x02.employeeGql.entity.Employee
import seg3x02.employeeGql.repository.EmployeesRepository
import seg3x02.employeeGql.resolvers.types.CreateEmployeeInput

@Controller
class EmployeesResolver(
        private val employeeRepository: EmployeesRepository,
        private val mongoOperations: MongoOperations
) {
    // Query to retrieve all employees
    @QueryMapping
    fun employees(): List<Employee> {
        return employeeRepository.findAll()
    }

    // Query to retrieve an employee by ID
    @QueryMapping
    fun employeeById(@Argument employeeId: String): Employee? {
        val employee = employeeRepository.findById(employeeId)
        return employee.orElse(null)
    }

    // Query to retrieve an employee by name
    @QueryMapping
    fun employeeByName(@Argument name: String): Employee? {
        val query = Query()
        query.addCriteria(Criteria.where("name").`is`(name))
        val result = mongoOperations.find(query, Employee::class.java)
        return result.firstOrNull()
    }

    // Mutation to create a new employee
    @MutationMapping
    fun newEmployee(@Argument("createEmployeeInput") input: CreateEmployeeInput): Employee {
        if (input.name != null &&
                        input.dateOfBirth != null &&
                        input.city != null &&
                        input.salary != null
        ) {
            val employee =
                    Employee(
                            name = input.name,
                            dateOfBirth = input.dateOfBirth,
                            city = input.city,
                            salary = input.salary,
                            gender = input.gender,
                            email = input.email
                    )
            employee.id = UUID.randomUUID().toString()
            employeeRepository.save(employee)
            return employee
        } else {
            throw Exception("Invalid input")
        }
    }

    // Mutation to delete an employee
    @MutationMapping
    fun deleteEmployee(@Argument("employeeId") id: String): Boolean {
        employeeRepository.deleteById(id)
        return true
    }

    // Mutation to update an existing employee
    @MutationMapping
    fun updateEmployee(
            @Argument employeeId: String,
            @Argument("createEmployeeInput") input: CreateEmployeeInput
    ): Employee {
        val employee = employeeRepository.findById(employeeId)
        employee.ifPresent {
            if (input.name != null) {
                it.name = input.name
            }
            if (input.dateOfBirth != null) {
                it.dateOfBirth = input.dateOfBirth
            }
            if (input.city != null) {
                it.city = input.city
            }
            if (input.salary != null) {
                it.salary = input.salary
            }
            if (input.gender != null) {
                it.gender = input.gender
            }
            if (input.email != null) {
                it.email = input.email
            }
            employeeRepository.save(it)
        }
        return employee.get()
    }
}
