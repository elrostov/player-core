package spring.app.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import spring.app.dao.abstraction.AuthorDao;
import spring.app.model.Author;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class AuthorDaoImpl extends AbstractDao<Long, Author> implements AuthorDao {

    public AuthorDaoImpl() {
        super(Author.class);
    }

    @Override
    public Author getByName(String name) {
        TypedQuery<Author> query = entityManager.createQuery("FROM Author WHERE name = :name", Author.class);
        query.setParameter("name", name);
        Author author;
        try {
            author = query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return author;
    }

    @Override
    public boolean isExist(String name) {
        TypedQuery<Author> query = entityManager.createQuery("FROM Author WHERE name = :name", Author.class);
        query.setParameter("name", name);
        List list = query.getResultList();
        if(list.isEmpty()){
            return false;
        }
        return true;
    }
}