package dao;

import java.util.List;

public interface Dao <E>{
    Integer save(E e);
    List<E> findAll();

}
