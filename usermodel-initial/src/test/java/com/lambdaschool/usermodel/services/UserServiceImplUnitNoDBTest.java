package com.lambdaschool.usermodel.services;

import com.lambdaschool.usermodel.UserModelApplicationTest;
import com.lambdaschool.usermodel.exceptions.ResourceNotFoundException;
import com.lambdaschool.usermodel.models.Role;
import com.lambdaschool.usermodel.models.User;
import com.lambdaschool.usermodel.models.UserRoles;
import com.lambdaschool.usermodel.models.Useremail;
import com.lambdaschool.usermodel.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.ArgumentMatchers.any;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserModelApplicationTest.class,
    properties = {"command.line.runner.enabled=false"})
public class UserServiceImplUnitNoDBTest
{
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userrepos;

    @MockBean
    private RoleService roleService;

    private List<User> userList = new ArrayList<>();
    @Before
    public void setUp() throws Exception
    {
        Role r1 = new Role("admin");
        Role r2 = new Role("user");
        Role r3 = new Role("data");
        r1.setRoleid(1);
        r2.setRoleid(2);
        r3.setRoleid(3);

        // admin, data, user
        User u1 = new User("admin",
            "password",
            "admin@lambdaschool.local");
        u1.setUserid(10);

        u1.getRoles()
            .add(new UserRoles(u1,
                r1));
        u1.getRoles()
            .add(new UserRoles(u1,
                r2));
        u1.getRoles()
            .add(new UserRoles(u1,
                r3));
        u1.getUseremails()
            .add(new Useremail(u1,
                "admin@email.local"));
        u1.getUseremails()
            .add(new Useremail(u1,
                "admin@mymail.local"));

        userList.add(u1);

        // data, user
        User u2 = new User("cinnamon",
            "1234567",
            "cinnamon@lambdaschool.local");
        u2.setUserid(20);
        u2.getRoles()
            .add(new UserRoles(u2,
                r2));
        u2.getRoles()
            .add(new UserRoles(u2,
                r3));
        u2.getUseremails()
            .add(new Useremail(u2,
                "cinnamon@mymail.local"));
        u2.getUseremails()
            .add(new Useremail(u2,
                "hops@mymail.local"));
        u2.getUseremails()
            .add(new Useremail(u2,
                "bunny@email.local"));
        userList.add(u2);

        // user
        User u3 = new User("barnbarn",
            "ILuvM4th!",
            "barnbarn@lambdaschool.local");
        u3.setUserid(30);
        u3.getRoles()
            .add(new UserRoles(u3,
                r2));
        u3.getUseremails()
            .add(new Useremail(u3,
                "barnbarn@email.local"));
        userList.add(u3);

        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void findUserById()
    {
        Mockito.when(userrepos.findById(4L))
            .thenReturn(Optional.of(userList.get(0)));

        assertEquals("admin",
            userService.findUserById(4)
                .getUsername());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void findUserByIdNotFound()
    {
        Mockito.when(userrepos.findById(10000L))
            .thenThrow(ResourceNotFoundException.class);

        assertEquals("admin",
            userService.findUserById(10000)
                .getUsername());
    }

    @Test
    public void findByNameContaining()
    {
        Mockito.when(userrepos.findByUsernameContainingIgnoreCase("bob"))
            .thenReturn(userList);

        assertEquals(3, userService.findByNameContaining("bob").size());
    }

    @Test
    public void findAll()
    {
        Mockito.when(userrepos.findAll())
            .thenReturn(userList);

        assertEquals(3,
            userService.findAll()
                .size());
    }

    @Test
    public void delete()
    {
        Mockito.when(userrepos.findById(4L))
            .thenReturn(Optional.of(userList.get(1)));

        Mockito.doNothing()
            .when(userrepos)
            .deleteById(4L);

        userService.delete(4);
        assertEquals(3, userList.size());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void deletefailed()
    {
        Mockito.when(userrepos.findById(777L))
            .thenReturn(Optional.empty());

        Mockito.doNothing()
            .when(userrepos)
            .deleteById(777L);

        userService.delete(777);
        assertEquals(3, userList.size());
    }

    @Test
    public void findByName()
    {
        Mockito.when(userrepos.findByUsername("admin"))
            .thenReturn(userList.get(0));

        assertEquals("admin",
            userService.findByName("admin").getUsername());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void findByNameNotFound()
    {
        Mockito.when(userrepos.findByUsername("turtle"))
            .thenThrow(ResourceNotFoundException.class);

        assertEquals("turtle",
            userService.findByName("barnbarn").getUsername());
    }

    @Test
    public void save()
    {
        User u = new User("alden",
            "password",
            "alden@gmail.com");

        Role r1 = new Role("admin");
        r1.setRoleid(3);

        u.getRoles()
            .add(new UserRoles(u,
                r1));
        u.getUseremails()
            .add(new Useremail(u,
                "admin@email.local"));

        Mockito.when(roleService.findRoleById(1))
            .thenReturn(r1);
        Mockito.when(userrepos.save(any(User.class)))
            .thenReturn(u);

        User addUser = userService.save(u);
        assertNotNull(addUser);
        assertEquals(u.getUsername(), addUser.getUsername());
    }

    @Test
    public void saveput()
    {
        User u = new User("alden",
            "password",
            "alden@gmail.com");
        u.setUserid(15
        );
        Role r1 = new Role("admin");
        r1.setRoleid(3);

        u.getRoles()
            .add(new UserRoles(u,
                r1));
        u.getUseremails()
            .add(new Useremail(u,
                "admin@email.local"));

        Mockito.when(userrepos.findById(15L))
            .thenReturn(Optional.of(u));
        Mockito.when(roleService.findRoleById(1))
            .thenReturn(r1);
        Mockito.when(userrepos.save(any(User.class)))
            .thenReturn(u);

        assertEquals(15L, userService.save(u).getUserid());


    }

    @Test(expected = ResourceNotFoundException.class)
    public void saveputfailed()
    {
        User u = new User("alden",
            "password",
            "alden@gmail.com");
        u.setUserid(15);
        Role r1 = new Role("admin");
        r1.setRoleid(3);

        u.getRoles()
            .add(new UserRoles(u,
                r1));
        u.getUseremails()
            .add(new Useremail(u,
                "admin@email.local"));

        Mockito.when(userrepos.findById(15L))
            .thenThrow(ResourceNotFoundException.class);
        Mockito.when(roleService.findRoleById(1))
            .thenReturn(r1);
        Mockito.when(userrepos.save(any(User.class)))
            .thenReturn(u);

        assertEquals(15L, userService.save(u).getUserid());
    }

    @Test
    public void update()
    {
        User u = new User("alden",
            "password",
            "alden@gmail.com");
        u.setUserid(15);

        Role r1 = new Role("admin");
        r1.setRoleid(3);

        u.getRoles()
            .add(new UserRoles(u,
                r1));
        u.getUseremails()
            .add(new Useremail(u,
                "admin@email.local"));


        Mockito.when(userrepos.findById(15L))
            .thenReturn(Optional.of(u));
        Mockito.when(roleService.findRoleById(1))
            .thenReturn(r1);
        Mockito.when(userrepos.save(any(User.class)))
            .thenReturn(u);

        User addUser = userService.update(u, 15);

        assertNotNull(addUser);
        assertEquals(u.getUsername(), addUser.getUsername());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void updateNotFound()
    {
        User u = new User("alden",
            "password",
            "alden@gmail.com");
        u.setUserid(777);

        Role r1 = new Role("admin");
        r1.setRoleid(3);

        u.getRoles()
            .add(new UserRoles(u,
                r1));
        u.getUseremails()
            .add(new Useremail(u,
                "admin@email.local"));


        Mockito.when(userrepos.findById(777L))
            .thenThrow(ResourceNotFoundException.class);
        Mockito.when(roleService.findRoleById(1))
            .thenReturn(r1);
        Mockito.when(userrepos.save(any(User.class)))
            .thenReturn(u);

        User addUser = userService.update(u, 15);

        assertNotNull(addUser);
        assertEquals(u.getUsername(), addUser.getUsername());
    }

    @Test
    public void deleteAll()
    {
    }
}