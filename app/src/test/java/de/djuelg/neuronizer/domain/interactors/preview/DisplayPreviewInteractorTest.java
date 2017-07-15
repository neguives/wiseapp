package de.djuelg.neuronizer.domain.interactors.preview;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.djuelg.neuronizer.domain.executor.Executor;
import de.djuelg.neuronizer.domain.executor.MainThread;
import de.djuelg.neuronizer.domain.interactors.exception.ExceptionId;
import de.djuelg.neuronizer.domain.interactors.preview.impl.DisplayPreviewInteractorImpl;
import de.djuelg.neuronizer.domain.model.Color;
import de.djuelg.neuronizer.domain.model.Deadline;
import de.djuelg.neuronizer.domain.model.ItemsPerPreview;
import de.djuelg.neuronizer.domain.model.TodoList;
import de.djuelg.neuronizer.domain.model.TodoListHeader;
import de.djuelg.neuronizer.domain.model.TodoListItem;
import de.djuelg.neuronizer.domain.model.TodoListPreview;
import de.djuelg.neuronizer.domain.repository.PreviewRepository;
import de.djuelg.neuronizer.threading.TestMainThread;

import static org.mockito.Mockito.when;


/**
 * Tests our DisplayPreviewInteractor.
 */
public class DisplayPreviewInteractorTest {

    private MainThread mainThread;
    @Mock private Executor executor;
    @Mock private PreviewRepository repository;
    @Mock private DisplayPreviewInteractor.Callback mockedCallback;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mainThread = new TestMainThread();
    }

    @After
    public void tearDown() throws Exception {
        repository.close();
    }

    @Test
    public void testNoPreviewExisting() throws Exception {
        DisplayPreviewInteractorImpl interactor = new DisplayPreviewInteractorImpl(executor, mainThread, mockedCallback, repository);
        interactor.run();

        Mockito.when(repository.getPreviews(new ItemsPerPreview(2)))
                .thenReturn(null);

        Mockito.verify(repository).getPreviews(new ItemsPerPreview(2));
        Mockito.verifyNoMoreInteractions(repository);
        Mockito.verify(mockedCallback).onRetrievalFailed(ExceptionId.NO_LISTS);
    }

    @Test
    public void testPreviewFound() throws Exception {

        List<TodoListPreview> previews = new ArrayList<>(1);
        previews.add(createPreview());

        when(repository.getPreviews(new ItemsPerPreview(2)))
                .thenReturn(previews);

        DisplayPreviewInteractorImpl interactor = new DisplayPreviewInteractorImpl(executor, mainThread, mockedCallback, repository);
        interactor.run();

        Mockito.verify(repository).getPreviews(new ItemsPerPreview(2));
        Mockito.verifyNoMoreInteractions(repository);
        Mockito.verify(mockedCallback).onPreviewsRetrieved(previews);
    }

    private TodoListPreview createPreview() {
        TodoList todoList = new TodoList("Hallo", 0);
        TodoListHeader header = new TodoListHeader("2","Header1", new Date(), new Date(), 0, new Color(1), "1");
        TodoListItem item = new TodoListItem("3", "Item1", new Date(), new Date(), 0, new Deadline(new Date()), false, "1", "2", "");
        List<TodoListItem> items = new ArrayList<>(1);
        items.add(item);

        return new TodoListPreview(todoList, header, items);
    }
}