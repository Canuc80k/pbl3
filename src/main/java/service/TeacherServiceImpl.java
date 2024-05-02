
package service;

import dao.TeacherDAO;
import dao.TeacherDAOImpl;
import java.util.List;
import model.people.teacher.Teacher;

public class TeacherServiceImpl implements TeacherService{
    private TeacherDAO temp=null;
    
    public TeacherServiceImpl(){
        temp=new TeacherDAOImpl();
    }
    @Override
    public List<Teacher> getList() {
      
        return temp.getList();
    }

    @Override
    public int createOrUpdate(Teacher teacher) {
       return temp.createOrUpdate(teacher);
    }
    

    @Override
    public int create(Teacher teacher) {
        return temp.create(teacher);
    }

    @Override
    public int update(Teacher teacher) {
        return temp.update(teacher);
    }
}
