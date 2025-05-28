package annak.hc.repository;

import annak.hc.entity.Order;
import annak.hc.entity.User;
import annak.hc.entity.embedded.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUserOrderByFormationDate(User user);
    List<Order> findAllByStatusOrderByFormationDate(Status status);
    List<Order> findAllByUserPhoneOrderByFormationDate(Long userPhone);
}
