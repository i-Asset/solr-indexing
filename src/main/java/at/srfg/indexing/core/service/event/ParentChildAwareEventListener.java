package at.srfg.indexing.core.service.event;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import at.srfg.indexing.core.service.repository.ClassRepository;
import at.srfg.indexing.model.common.ClassType;
import at.srfg.indexing.model.common.IPropertyAware;
import at.srfg.indexing.model.common.PropertyType;

@Component
public class ParentChildAwareEventListener {

	@Autowired
	private ClassRepository classRepo;

	/**
	 * Asynchronous event listener processing the removal of {@link IPropertyAware}
	 * objects
	 * 
	 * @param event
	 */
	@Async
	@EventListener
	public void onClassTypeRemove(RemoveParentChildAwareEvent event) {
		ClassType item = event.getEventObject();

		Collection<String> parents = item.getParents();
		// handle the parents
		parents.forEach(new Consumer<String>() {

			@Override
			public void accept(String t) {
				Optional<ClassType> classType = classRepo.findById(t);
				if (classType.isPresent()) {
					ClassType pt = classType.get();
					if (pt.getChildren().contains(item.getUri())) {
						pt.removeChild(item.getUri());
						pt.removeAllChild(item.getUri());
						classRepo.save(pt);
					}
				}
			}
		});
		Collection<String> allParents = item.getAllParents();
		// no need to process them as well
		allParents.removeAll(parents);
		// handle the all parents
		allParents.forEach(new Consumer<String>() {

			@Override
			public void accept(String t) {
				Optional<ClassType> classType = classRepo.findById(t);
				if (classType.isPresent()) {
					ClassType pt = classType.get();
					if (pt.getAllChildren().contains(item.getUri())) {
						pt.removeAllChild(item.getUri());
						classRepo.save(pt);
					}
				}
			}
		});
		// handle children
		Collection<String> children = item.getChildren();
		children.forEach(new Consumer<String>() {

			@Override
			public void accept(String t) {
				Optional<ClassType> classType = classRepo.findById(t);
				if (classType.isPresent()) {
					ClassType pt = classType.get();
					if (pt.getParents().contains(item.getUri())) {
						pt.removeParent(item.getUri());
						pt.removeAllParent(item.getUri());
						classRepo.save(pt);
					}
				}
			}
		});
		// obtain all children
		Collection<String> allChildren = item.getAllChildren();
		// no need to process direct children again
		allChildren.removeAll(children);
		// handle all children
		allChildren.forEach(new Consumer<String>() {

			@Override
			public void accept(String t) {
				Optional<ClassType> classType = classRepo.findById(t);
				if (classType.isPresent()) {
					ClassType pt = classType.get();
					if (pt.getParents().contains(item.getUri())) {
						pt.removeParent(item.getUri());
						pt.removeAllParent(item.getUri());
						classRepo.save(pt);
					}
				}
			}
		});

	}

	/**
	 * Synchronous event listener maintaining the link between {@link ClassType}
	 * entries and their {@link PropertyType}s.
	 * 
	 * @param event
	 */
	@EventListener
	public void onApplicationEvent(ParentChildAwareEvent event) {
		ClassType item = event.getEventObject();

		Collection<String> parents = item.getParents();
		// handle the parents
		parents.forEach(new Consumer<String>() {

			@Override
			public void accept(String t) {
				Optional<ClassType> classType = classRepo.findById(t);
				if (classType.isPresent()) {
					ClassType pt = classType.get();
					pt.addChild(item.getUri());
					pt.addAllChild(item.getUri());
					classRepo.save(pt);
					processAllParents(pt, item.getUri());
				}
			}
		});
		// handle children
		Collection<String> children = item.getChildren();
		children.forEach(new Consumer<String>() {

			@Override
			public void accept(String t) {
				Optional<ClassType> classType = classRepo.findById(t);
				if (classType.isPresent()) {
					ClassType pt = classType.get();
					pt.addParent(item.getUri());
					pt.addAllParent(item.getUri());
					classRepo.save(pt);
					processAllChildren(pt, item.getUri());
				}
			}
		});
	}

	private void processAllParents(ClassType parentClass, String itemUri) {
		Collection<String> allParents = parentClass.getAllParents();
		if ( allParents != null && !allParents.isEmpty()) {
			for (String allParent : allParents) {
				Optional<ClassType> classType = classRepo.findById(allParent);
				if (classType.isPresent()) {
					ClassType pt = classType.get();
					pt.addAllChild(itemUri);
					classRepo.save(pt);
				}
			}
		}
	}
	private void processAllChildren(ClassType parentClass, String itemUri) {
		Collection<String> allChildren = parentClass.getAllChildren();
		if ( allChildren != null && ! allChildren.isEmpty()) {
			for (String allParent : allChildren) {
				Optional<ClassType> classType = classRepo.findById(allParent);
				if (classType.isPresent()) {
					ClassType pt = classType.get();
					pt.addAllParent(itemUri);
					classRepo.save(pt);
				}
			}
		}
	}
}
