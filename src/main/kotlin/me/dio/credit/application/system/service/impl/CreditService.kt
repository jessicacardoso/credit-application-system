package me.dio.credit.application.system.service.impl

import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.exception.BusinessException
import me.dio.credit.application.system.repository.CreditRepository
import me.dio.credit.application.system.service.ICreditService
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.util.*

@Service
class CreditService(
  private val creditRepository: CreditRepository,
  private val customerService: CustomerService
): ICreditService {
  override fun save(credit: Credit): Credit {
    credit.apply {
      customer = customerService.findById(credit.customer?.id!!)
    }
  // a primeira parcela não pode ser depois de 90 dias
    if (credit.dayFirstInstallment.isAfter(LocalDate.now().plusDays(90))) {
      throw BusinessException("The first installment cannot be after 90 days")
    }
    return this.creditRepository.save(credit)
  }

  override fun findAllByCustomer(customerId: Long): List<Credit> =
    this.creditRepository.findAllByCustomerId(customerId)

  override fun findByCreditCode(customerId: Long, creditCode: UUID): Credit {
    val credit: Credit = (this.creditRepository.findByCreditCode(creditCode)
      ?: throw BusinessException("Creditcode $creditCode not found"))
    return if (credit.customer?.id == customerId) credit else throw IllegalArgumentException("Contact admin")
    /*if (credit.customer?.id == customerId) {
      return credit
    } else {
      throw RuntimeException("Contact admin")
    }*/
  }
}