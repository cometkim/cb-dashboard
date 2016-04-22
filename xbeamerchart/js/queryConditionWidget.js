/**
 * Created by 'Hyeseong Kim<hyeseong.kim@architectgroup.com>' on 2016-03-14.
 * Modified from codeBeamer 7.9.0-RC source.
 */

var xbeamerchart = xbeamerchart || {};
xbeamerchart.QueryConditionWidget = function() {
	var fieldIdMap = {
		"Status" : 7,
		"workItemStatus" : 7,
		"Priority" : 2,
		"Category" : 13,
		"Severity" : 14,
		"Resolution" : 15,
		"workItemResolution" : 15,
		"Submitted at" : 4,
		"Modified at" : 74,
		"Start Date" : 8,
		"End Date" : 9,
		"Summary" : 3,
		"Description" : 80,
		"Assigned to" : 5,
		"Owner" : 32,
		"Submitted by": 6,
		"Story Points" : 19
	};

	var cbQLDateAttrMap = {
		"Submitted at" : "submittedAt",
		"Modified at" : "modifiedAt",
		"Start Date" : "startDate",
		"End Date" : "endDate"
	};
	var cbQLDateAttrs = ["submittedAt", "modifiedAt", "startDate", "endDate"];

	var cbQLUserAttrMap = {
		"Submitted by" : "submittedBy",
		"Assigned to" : "assignedTo",
		"Owner" : "owner"
	};
	var cbQLUserAttrs = ["submittedBy", "assignedTo", "owner"];

	var cbQLTextAttrs = ["Summary", "Description"];

	var cbQLReferenceAttrs = ["referenceToId", "referenceFromId"];

	var cbQLNumberAttrMap = {
		"Story Points" : "storyPoints"
	};
	var cbQLNumberAttrs = ["storyPoints"];

	var cbQLMeaningAttrs = ["workItemStatus", "workItemResolution"];

	var orderBy = {
		assignedTo : i18n.message("tracker.field.Assigned to.label"),
		endDate :  i18n.message("tracker.field.End Date.label"),
		modifiedAt :  i18n.message("tracker.field.Modified at.label"),
		priority :  i18n.message("tracker.field.Priority.label"),
		resolution :  i18n.message("tracker.field.Resolution.label"),
		severity :  i18n.message("tracker.field.Severity.label"),
		startDate :  i18n.message("tracker.field.Start Date.label"),
		status :  i18n.message("tracker.field.Status.label"),
		submittedAt :  i18n.message("tracker.field.Submitted at.label"),
		submittedBy :  i18n.message("tracker.field.Submitted by.label"),
		summary :  i18n.message("tracker.field.Summary.label"),
		"tracker.id" : i18n.message("query.condition.widget.order.by.tracker"),
		"item.id" : i18n.message("query.condition.widget.order.by.item.id")
	};

	

	function startsWith(string, prefix) {
		return string.slice(0, prefix.length) == prefix;
	}

	var reloadIfProjectChange = function() {
		var that = this;
		that.getTrackersJSON(function(result) {
			// Reload trackers
			that.settings.widgetContainer.find(".trackerSelector").each(function() {
				var originalValues = $(this).val();
				that.renderTrackers(result, $(this));
				if (originalValues && originalValues.length > 0) {
					that.checkSelectedValues($(this), originalValues);
				} else {
					that.checkFirstOption($(this));
				}
				$(this).multiselect("refresh");
				that.reloadIfTrackerChange();
			});
		});
		that.getUserChoicesJSON(function(result) {
			// Reload user fields
			that.settings.widgetContainer.find(".userSelector").each(function() {
				var originalValues = $(this).val();
				var fieldName = $(this).data("fieldName");
				that.renderUsers(fieldName, result, $(this));
				if (originalValues && originalValues.length > 0) {
					that.checkSelectedValues($(this), originalValues);
				} else {
					that.checkFirstOption($(this));
				}
				$(this).multiselect("refresh");
			});
		});
	};

	var reloadIfTrackerChange = function() {
		var that = this;
		that.getFields();
		that.settings.widgetContainer.find(".choiceFieldSelector").each(function() {
			var $choiceFieldSelector = $(this);
			var originalValues = $choiceFieldSelector.val();
			var fieldId = $choiceFieldSelector.data("fieldId");
			that.getFieldChoicesJSON(fieldId, function(result) {
				that.renderFieldChoices(result, $choiceFieldSelector);
				if (originalValues && originalValues.length > 0) {
					that.checkSelectedValues($choiceFieldSelector, originalValues);
				} else {
					that.checkFirstOption($choiceFieldSelector);
				}
				$choiceFieldSelector.multiselect("refresh");
			});
		});
	};

	var destroyMultiSelect = function($selector, type, fieldId) {
		var that = this;
		$selector.multiselect("destroy");
		$selector.remove();
		if (type == "project" || type == "tracker") {
			var index = $.inArray(type + "s", that.menuItemsDisabled);
			if (index > -1) {
				that.menuItemsDisabled.splice(index, 1);
			}
			if (type == "project") {
				that.reloadIfProjectChange();
			}
			if (type == "tracker") {
				that.reloadIfTrackerChange();
			}
		} else {
			var index = $.inArray(parseInt(fieldId, 10), that.menuItemsDisabled);
			if (index > -1) {
				that.menuItemsDisabled.splice(index, 1);
			}
		}
	};

	var initMultiSelect = function($selector, type, label, cbQLAttrName, fieldId, deletable) {
		var that = this;
		$selector.data("cbQLAttrName", cbQLAttrName);
		if (fieldId) {
			$selector.data("fieldId", fieldId);
		}
		$selector.multiselect({
			classes: "queryConditionSelector " + type,
			minWidth: 100,
			checkAllText : "",
			uncheckAllText: "",
			selectedText: function(numChecked, numTotal, checkedItems) {
				var value = [];
				var $checkedItems = $(checkedItems);
				if ($checkedItems.first().closest("li").hasClass("anyValue")) {
					return "<b>" + label + ":</b> " + i18n.message("tracker.field.value.any");
				}
				$checkedItems.each(function(){
					var valueString = $(this).next().html();
					if ($checkedItems.first().closest(".queryConditionSelector").hasClass("user")
						&& !$(this).closest("li").hasClass("roleOption") && !$(this).closest("li").hasClass("currentUser")) {
						// trim name and email domain, only show username
						valueString = valueString.substring(0, valueString.indexOf(' ('));
					}
					value.push(valueString);
				});
				var joinedText = value.join(", ");
				if (joinedText.length > 100) {
					joinedText = joinedText.substring(0, 100) + "...";
				}
				return "<b>" + label + ":</b> " + joinedText;
			},
			noneSelectedText: function() {
				return "<b>" + label + ":</b> - ";
			},
			create: function(event, ui) {
				// remove check/uncheck all/close buttons
				$(".queryConditionSelector .ui-multiselect-all").closest("ul").remove();

				if (deletable) {
					var _that = $(this);
					var $button = $(this).multiselect("getButton");
					var $deleteButton = $("<span>",{ "class" : "ui-icon removeIcon"});
					$deleteButton.data("fieldId", fieldId);
					$deleteButton.click(function(e) {
						e.preventDefault();
						that.destroyMultiSelect(_that, type, $(this).data("fieldId"));
					});
					$button.prepend($deleteButton);
				}

			},
			click: function(event, ui) {
				var $menu = $(this).multiselect("widget");
				if (ui.value != "on") {
					var selected = $(this).multiselect("getChecked").filter(":not(.showHiddenCheckBox)");
					if (selected.length == 0) {
						$menu.find("[value=0]").attr("checked", true);
					}
					if (ui.checked) {
						if (ui.value == 0) {
							$menu.find(":checkbox:checked").filter(":not(.showHiddenCheckBox)").each(function() {
								var $that = $(this);
								if ($that.val() != 0 && $that.is(":checked")) {
									$that.attr("checked", false);
								}
							});
						} else {
							$menu.find("[value=0]").attr("checked", false);
						}
					} else if (!ui.checked && ui.value == 0) {
						$menu.find("[value=0]").attr("checked", true);
					}
				}
			}
		}).multiselectfilter({
			label: "",
			placeholder: i18n.message("Filter")
		});
	};

	var checkSelectedValues = function($selector, values) {
		var that = this;
		for(var i=0; i < values.length; i++) {
			var optionVal = values[i];
			$selector.find('option[value="' + optionVal + '"]').prop("selected", "selected");
		}
		$selector.multiselect("refresh");
	};

	var checkFirstOption = function($selector) {
		var that = this;
		$selector.find("option").first().prop("selected", "selected");
		$selector.multiselect("refresh");
	};

	var getProjects = function(callback) {
		var that = this;
		//console.log(that);
		that.showAjaxLoading();
		$.getJSON(that.settings.getProjectsUrl).done(function(result) {
			var recent = result.recent;
			var all = result.all;
			var $selector = $("<select>", { "class" : "selector projectSelector", multiple : "multiple"});
			$selector.append($("<option>", { value: 0, "class": "anyValue" }).text(i18n.message("tracker.field.value.any")));
			var $recentGroup = $("<optgroup>", { label: i18n.message("recent.projects.label") });
			for (var i=0; i < recent.length; i++) {
				var project = recent[i];
				$recentGroup.append($("<option>", { value : project.id }).text(project.name));
			}
			$selector.append($recentGroup);
			var $allGroup = $("<optgroup>", { label: i18n.message("my.open.issues.all.projects") });
			for (var i=0; i < all.length; i++) {
				var project = all[i];
				$allGroup.append($("<option>", { value : project.id }).text(project.name));
			}
			$selector.append($allGroup);
			that.settings.addButton.before($selector);
			that.initMultiSelect($selector, "project", i18n.message("project.label"), "project.id", false, false);
			$selector.change(function() {
				that.reloadIfProjectChange();
			});
			if (that.initData.hasOwnProperty("projectIds") && that.initData.projectIds.length > 0) {
				that.checkSelectedValues($selector, that.initData.projectIds);
			} else {
				that.checkFirstOption($selector);
			}
			that.hideAjaxLoading();
			callback();
		});
	};

	var renderTrackers = function(result, $selector, withoutAny) {
		var that = this;
		$selector.empty();
		if (!withoutAny) {
			$selector.append($("<option>", { value: 0, "class": "anyValue" }).text(i18n.message("tracker.field.value.any")));
		}
		for (var i=0; i < result.length; i++) {
			var project = result[i];
			var $projectGroup = $("<optgroup>", { label: i18n.message("project.label") + ": " + project.name });
			var trackers = project.trackers;
			for (var j=0; j < trackers.length; j++) {
				var tracker = trackers[j];
				if (!tracker.hidden) {
					var $option = $("<option>", { value : tracker.id, "data-hidden":  tracker.hidden }).text(tracker.name);
					$projectGroup.append($option);
				}
			}
			$selector.append($projectGroup);
		}
	};

	var getTrackersJSON = function(callback, withoutProjects) {
		var that = this;
		var projectIds = withoutProjects ? [] : that.getProjectIds();
		that.showAjaxLoading();
		$.getJSON(that.settings.getTrackersUrl, { project_id_list : projectIds.join(",")}).done(function(result) {
			that.hideAjaxLoading();
			callback(result);
		});
	};

	var getTrackers = function(callback) {
		var that = this;

		that.getTrackersJSON(function(result) {
			var $selector = $("<select>", { "class" : "selector trackerSelector", multiple : "multiple"});
			that.renderTrackers(result, $selector);
			that.settings.addButton.before($selector);
			that.initMultiSelect($selector, "tracker", i18n.message("tracker.label"), "tracker.id", false, false);
			$selector.change(function() {
				that.reloadIfTrackerChange();
			});
			if (that.initData.hasOwnProperty("trackerIds") && that.initData.trackerIds.length > 0) {
				that.checkSelectedValues($selector, that.initData.trackerIds);
			} else {
				that.checkFirstOption($selector);
			}
			callback();
		});
	};

	var renderFieldChoices = function(result, $selector) {
		var that = this;

		$selector.empty();
		$selector.append($("<option>", { value: 0, "class": "anyValue" }).text(i18n.message("tracker.field.value.any")));
		if (result.hasOwnProperty("meanings")) {
			var $meaningOptGroup = $("<optgroup>", { "class": "meaning", "label" : i18n.message("tracker.choice.flags.label")});
			var cbQlAttrName = result.hasOwnProperty("meaningCbQLAttrName") ? result.meaningCbQLAttrName : "";
			$meaningOptGroup.data("cbQlAttrName", cbQlAttrName);
			for (var i=0; i < result.meanings.length; i++) {
				var meaning = result.meanings[i];
				$meaningOptGroup.append($("<option>", { "class" : "meaningOption", value : meaning.id }).text(meaning.name));
			}
			$selector.append($meaningOptGroup);
		}
		if (result.hasOwnProperty("choices")) {
			var optionsByTracker = result.choices;
			for (var i=0; i < optionsByTracker.length; i++) {
				var tracker = optionsByTracker[i];
				var $trackerOptGroup = $("<optgroup>", { "label" : tracker.name });
				var options = tracker.fieldOptions;
				for (var j=0; j < options.length; j++) {
					var option = options[j];
					var optionValueId = "" + tracker.projectId + "_" + tracker.trackerId + "_" + option.name;
					$trackerOptGroup.append($("<option>", { value : optionValueId }).text(option.name));
				}
				$selector.append($trackerOptGroup);
			}
		}
	};

	var renderUsers = function(fieldName, result, $selector) {
		var that = this;

		$selector.empty();
		$selector.append($("<option>", { value: 0, "class": "anyValue" }).text(i18n.message("tracker.field.value.any")));
		$selector.append($("<option>", { value: -1, "class": "currentUser" }).text(i18n.message("query.condition.widget.current.user")));
		if (result.hasOwnProperty("users")) {
			var $usersOptGroup = $("<optgroup>", { "label" : i18n.message("tracker.field.Users.label")});
			for (var i=0; i < result.users.length; i++) {
				var user = result.users[i];
				$usersOptGroup.append($("<option>", { value : user.id }).text(user.aliasName + " (" + user.realName + (user.emailDomain.length > 0 ? ", " + user.emailDomain : "") + ")"));
			}
			$selector.append($usersOptGroup);
		}
		if (cbQLUserAttrMap[fieldName] != "submittedBy" && result.hasOwnProperty("roles")) {
			var $roleOptGroup = $("<optgroup>", { "label" : i18n.message("tracker.fieldAccess.roles.label")});
			for (var i=0; i < result.roles.length; i++) {
				var role = result.roles[i];
				$roleOptGroup.append($("<option>", { value : role.roleId, "class": "roleOption" }).text(role.roleLabel));
			}
			$selector.append($roleOptGroup);
		}
	};

	var getFieldChoicesJSON = function(fieldId, callback) {
		var that = this;

		var projectIds = that.getProjectIds();
		var trackerIds = that.getTrackerIds();
		var data = {
			field_id: fieldId
		};
		if (projectIds.length > 0) {
			data["project_id_list"] = projectIds.join(",");
		}
		if (trackerIds.length > 0) {
			data["tracker_id_list"] = trackerIds.join(",");
		}
		that.showAjaxLoading();
		$.getJSON(that.settings.getFieldChoicesUrl, data).done(function(result) {
			that.hideAjaxLoading();
			callback(result);
		});
	};

	var getUserChoicesJSON = function(callback) {
		var that = this;

		var projectIds = that.getProjectIds();
		var data = {};
		if (projectIds.length > 0) {
			data["project_id_list"] = projectIds.join(",");
		}
		that.showAjaxLoading();
		$.getJSON(that.settings.getUsersUrl, data).done(function(result) {
			that.hideAjaxLoading();
			callback(result);
		});
	};

	var getFieldChoices = function(projectIds, trackerIds, fieldId, fieldName, fieldLabel) {
		var that = this;

		that.menuItemsDisabled.push(parseInt(fieldId, 10));

		var $selector = $("<select>", { "class" : "selector choiceFieldSelector", multiple : "multiple"});
		that.settings.addButton.before($selector);
		$selector.hide();

		that.getFieldChoicesJSON(fieldId, function(result) {

			$selector.show();
			that.renderFieldChoices(result, $selector);

			var cbQLAttrName = fieldName;
			that.initMultiSelect($selector, "choiceField", fieldLabel, cbQLAttrName, fieldId, true);
			var valuesLength = 0;
			if (that.initData.hasOwnProperty("choiceFields") && that.initData.choiceFields.hasOwnProperty(fieldName) && that.initData.choiceFields[fieldName].length > 0) {
				that.checkSelectedValues($selector, that.initData.choiceFields[fieldName]);
				valuesLength += that.initData.choiceFields[fieldName].length;
			}
			if (fieldId == 7 && that.initData.hasOwnProperty("choiceFields") && that.initData.choiceFields.hasOwnProperty("workItemStatus") && that.initData.choiceFields["workItemStatus"].length > 0) {
				that.checkSelectedValues($selector, that.initData.choiceFields["workItemStatus"]);
				valuesLength += that.initData.choiceFields["workItemStatus"].length;
			}
			if (fieldId == 15 && that.initData.hasOwnProperty("choiceFields") && that.initData.choiceFields.hasOwnProperty("workItemResolution") && that.initData.choiceFields["workItemResolution"].length > 0) {
				that.checkSelectedValues($selector, that.initData.choiceFields["workItemResolution"]);
				valuesLength += that.initData.choiceFields["workItemResolution"].length;
			}
			if (valuesLength == 0) {
				that.checkFirstOption($selector);
			}
		});
	};

	var getUserChoices = function(fieldId, fieldName, fieldLabel) {
		var that = this;

		that.menuItemsDisabled.push(parseInt(fieldId, 10));
		var $selector = $("<select>", { "class" : "selector userSelector", multiple : "multiple"});
		$selector.data("fieldName", fieldName);
		that.settings.addButton.before($selector);
		$selector.hide();
		that.getUserChoicesJSON(function(result) {

			$selector.show();
			that.renderUsers(fieldName, result, $selector);

			var userAttrName = cbQLUserAttrMap[fieldName];
			that.initMultiSelect($selector, "user", fieldLabel, userAttrName, fieldId, true);

			var valuesLength = 0;
			if (that.initData.hasOwnProperty("userFields") && that.initData.userFields.hasOwnProperty(userAttrName) && that.initData.userFields[userAttrName].length > 0) {
				that.checkSelectedValues($selector, that.initData.userFields[userAttrName]);
				valuesLength += that.initData.userFields[userAttrName].length;
			}
			if (that.initData.hasOwnProperty("userFields") && that.initData.userFields.hasOwnProperty(userAttrName + "Role") && that.initData.userFields[userAttrName + "Role"].length > 0) {
				that.checkSelectedValues($selector, that.initData.userFields[userAttrName + "Role"]);
				valuesLength += that.initData.userFields[userAttrName + "Role"].length;
			}
			if (valuesLength == 0) {
				that.checkFirstOption($selector);
			}

		});
	};

	var getProjectIds = function() {
		var that = this;

		var projectIds = [];
		that.settings.widgetContainer.find(".projectSelector").each(function () {
			var selected = $(this).multiselect("getChecked");
			selected.each(function () {
				var projectId = parseInt($(this).val(), 10);
				if (projectId != 0) {
					projectIds.push(projectId);
				}
			});
		});
		return $.unique(projectIds);
	};

	var getTrackerIds = function() {
		var that = this;

		var trackerIds = [];
		that.settings.widgetContainer.find(".trackerSelector").each(function () {
			var selected = $(this).multiselect("getChecked");
			selected.each(function () {
				var trackerId = parseInt($(this).val(), 10);
				if (trackerId != 0) {
					trackerIds.push(trackerId);
				}
			});
		});
		return $.unique(trackerIds);
	};

	var decodeReferences = function(values) {
		var that = this;

		var result = [];
		var parts = values.split(",");
		for (var i = 0; i < parts.length; i++) {
			var value = parts[i];
			result.push(that.decodeReferenceValue(value));
		}
		return result;
	};

	var decodeReferenceValue = function(value) {
		var that = this;

		var parts = value.split("-");
		return parseInt(parts[1], 10);
	};

	var getReferenceSelector = function(fieldLabel, cbQLAttrName, trackerItemIds) {
		var that = this;

		trackerItemIds = trackerItemIds || [];
		that.menuItemsDisabled.push(cbQLAttrName);

		var getReferenceProjectIds = function(field) {
			var projectIds = [];
			$('.referenceProjectSelector[data-field="' + field + '"]').each(function () {
				var selected = $(this).multiselect("getChecked");
				selected.each(function () {
					projectIds.push(parseInt($(this).val(), 10));
				});
			});
			return $.unique(projectIds);
		};

		var getReferenceTrackerIds = function(field) {
			var trackerIds = [];
			$('.referenceTrackerSelector[data-field="' + field + '"]').each(function () {
				var selected = $(this).multiselect("getChecked");
				selected.each(function () {
					trackerIds.push(parseInt($(this).val(), 10));
				});
			});
			return $.unique(trackerIds);
		};

		var reloadTrackerSelectorIfProjectChange = function(field) {
			var projectIds = getReferenceProjectIds(field);
			$.getJSON(that.settings.getTrackersUrl, { project_id_list : projectIds.join(",")}).done(function(result) {
				// Reload trackers
				$('.referenceTrackerSelector[data-field="' + field + '"]').each(function() {
					var originalValues = $(this).val();
					that.renderTrackers(result, $(this), true);
					if (originalValues && originalValues.length > 0) {
						that.checkSelectedValues($(this), originalValues);
					} else {
						that.checkFirstOption($(this));
					}
					$(this).multiselect("refresh");
				});
			});
		};

		var refreshReferenceButton = function($dialog) {
			var $widget = $dialog.dialog("widget");
			var references = [];
			$widget.find(".referenceSpan ul li.token-input-token-facebook").each(function() {
				references.push($(this).find("p").text());
			});
			var referencesString = references.join(", ");
			if (referencesString.length > 100) {
				referencesString = referencesString.substring(0, 100) + "...";
			}
			var value = $widget.find('.referenceSpan input[type="hidden"]').val();
			var $field = $("#referenceField_" + cbQLAttrName);
			$field.data("referenceValue", that.decodeReferences(value));
			$field.find(".labelValue").text(" " + referencesString);
			$dialog.dialog("close");
		};

		var initAutoComplete = function($dialog, field, existing) {
			$dialog.find('.referenceSpan[data-field="' + field + '"]').remove();
			var trackerIds = getReferenceTrackerIds(cbQLAttrName);
			var $referenceSpan = $("<span>", { "class" : "referenceSpan", "data-field": field});
			if (trackerIds.length > 0) {
				$referenceSpan.referenceField(existing, {
					workItemMode: true,
					multiple: true,
					workItemTrackerIds: trackerIds.join(",")
				});
				$dialog.append($referenceSpan);
				setTimeout(function() {
					$referenceSpan.referenceFieldAutoComplete();
					if (existing.length > 0) {
						refreshReferenceButton($dialog);
					}
				}, 300);
			} else {
				$referenceSpan.append($("<div>", { "class" : "warning"}).text(i18n.message("query.condition.widget.reference.select.tracker")));
				$dialog.append($referenceSpan);
			}
		};

		var loadDialog = function(projectIds, trackerIds, existingReferences) {
			var $dialog = $("<div>", { id: "referenceDialog_" + cbQLAttrName});
			$dialog.append($("<div>", { class: "referenceStep"}).html(i18n.message("query.condition.widget.reference.step1")));

			$.getJSON(that.settings.getProjectsUrl).done(function(result) {
				var recent = result.recent;
				var all = result.all;
				var $selector = $("<select>", { "class" : "selector referenceProjectSelector", multiple : "multiple", "data-field": cbQLAttrName});
				var $recentGroup = $("<optgroup>", { label: i18n.message("recent.projects.label") });
				for (var i=0; i < recent.length; i++) {
					var project = recent[i];
					$recentGroup.append($("<option>", { value : project.id }).text(project.name));
				}
				$selector.append($recentGroup);
				var $allGroup = $("<optgroup>", { label: i18n.message("my.open.issues.all.projects") });
				for (var i=0; i < all.length; i++) {
					var project = all[i];
					$allGroup.append($("<option>", { value : project.id }).text(project.name));
				}
				$selector.append($allGroup);
				$dialog.append($selector);
				that.initMultiSelect($selector, "referenceProject", i18n.message("project.label"), "", false, false);
				if (projectIds.length > 0) {
					that.checkSelectedValues($selector, projectIds);
				} else {
					that.checkFirstOption($selector);
				}
				$selector.change(function() {
					reloadTrackerSelectorIfProjectChange(cbQLAttrName);
				});

				$dialog.append($("<div>", { class: "referenceStep"}).html(i18n.message("query.condition.widget.reference.step2")));
				var existingProjectIds = getReferenceProjectIds(cbQLAttrName);
				$.getJSON(that.settings.getTrackersUrl, { project_id_list : existingProjectIds.join(",")}).done(function(trackerResult) {
					var $trackerSelector = $("<select>", { "class" : "selector referenceTrackerSelector", multiple : "multiple", "data-field" : cbQLAttrName});
					that.renderTrackers(trackerResult, $trackerSelector, true);
					$dialog.append($trackerSelector);
					that.initMultiSelect($trackerSelector, "referenceTracker", i18n.message("tracker.label"), "", false, false);
					if (trackerIds.length > 0) {
						that.checkSelectedValues($trackerSelector, trackerIds);
					} else {
						that.checkFirstOption($trackerSelector);
					}
					$trackerSelector.change(function() {
						initAutoComplete($dialog, cbQLAttrName, []);
					});

					$dialog.append($("<div>", { class: "referenceStep"}).html(i18n.message("query.condition.widget.reference.step3")));
					initAutoComplete($dialog, cbQLAttrName, existingReferences);
				});
			});

			$("body").append($dialog);

			$("#referenceDialog_" + cbQLAttrName).dialog({
				autoOpen: false,
				modal: true,
				dialogClass: 'popup',
				width: 600,
				title: fieldLabel,
				buttons: [
					{
						text: i18n.message("button.ok"),
						click: function() {
							refreshReferenceButton($(this));
						}
					},
					{
						text: i18n.message("button.cancel"),
						click: function() {
							$(this).dialog("close");
						}
					}
				]
			});
		};

		var $textField = $("<button>", { id: "referenceField_" + cbQLAttrName, "type": "button", "class" : "ui-multiselect ui-widget ui-state-default ui-corner-all queryConditionSelector referenceField" });
		$textField.data("cbQLAttrName", cbQLAttrName);
		$textField.append($("<span>", {"class" : "ui-icon ui-icon-triangle-1-s"}));

		var $deleteButton = $("<span>",{ "class" : "ui-icon removeIcon"});
		$deleteButton.click(function(e) {
			e.preventDefault();
			$(this).closest("button").remove();
			$("#referenceDialog_" + cbQLAttrName).remove();
			var index = $.inArray(cbQLAttrName, that.menuItemsDisabled);
			if (index > -1) {
				that.menuItemsDisabled.splice(index, 1);
			}
		});
		$textField.prepend($deleteButton);

		var $textFieldLabel = $("<span>").html("<b>" + fieldLabel + "</b>:");
		$textField.append($textFieldLabel);
		$textField.append($("<span>", { class: "labelValue"}).text(" -"));
		that.settings.addButton.before($textField);

		if (trackerItemIds.length > 0) {
			$.getJSON(that.settings.getExistingReferencesUrl, { "tracker_item_id_list": trackerItemIds.join(",") }).done(function(result) {
				loadDialog(result.projectIds, result.trackerIds, result.trackerItems);
			});
		} else {
			loadDialog([], [], []);
		}

		$textField.click(function(e) {
			e.stopPropagation();
			$("#referenceDialog_" + cbQLAttrName).dialog("open");
		});

	};

	var getTextFieldContainer = function(fieldId, fieldName, fieldLabel, isNot, conditionText) {
		var that = this;

		that.menuItemsDisabled.push(parseInt(fieldId, 10));

		var $textField = $("<button>", { id: "textField_" + fieldId, "type": "button", "class" : "ui-multiselect ui-widget ui-state-default ui-corner-all queryConditionSelector textField" });
		$textField.data("cbQLAttrName", fieldName);
		$textField.append($("<span>", {"class" : "ui-icon ui-icon-triangle-1-s"}));

		var $deleteButton = $("<span>",{ "class" : "ui-icon removeIcon"});
		$deleteButton.data("fieldId", fieldId);
		$deleteButton.click(function(e) {
			e.preventDefault();
			var fieldId = parseInt($(this).data("fieldId"), 10);
			$(this).closest("button").remove();
			var index = $.inArray(fieldId, that.menuItemsDisabled);
			if (index > -1) {
				that.menuItemsDisabled.splice(index, 1);
			}
		});
		$textField.prepend($deleteButton);

		var $textFieldLabel = $("<span>").html("<b>" + fieldLabel + "</b>:");
		$textField.append($textFieldLabel);
		$textField.append($("<span>", { class: "labelValue"}).text((isNot ? " " + i18n.message("query.condition.widget.not") : "") + " " + i18n.message("query.condition.widget.like") + " " + (conditionText && conditionText.length > 0 ? conditionText : "-")));
		that.settings.addButton.before($textField);

		var fixMenuPosition = function() {
			var $menu = $('.textFieldMenu[data-field-id="'+ fieldId + '"]');
			$menu.position({
				of: that.settings.widgetContainer.find("#textField_" + fieldId),
				my: "left top",
				at: "left bottom"
			});
		};

		var textFieldChangeHandler = function(element, fieldId) {
			var $menuCont = element.closest(".textFieldMenu");
			var isNot = $menuCont.find(".notCheckBox").is(":checked");
			var inputValue = $menuCont.find(".inputValue").val();
			var $field = that.settings.widgetContainer.find("#textField_" + fieldId);
			var label = (isNot ? " " + i18n.message("query.condition.widget.not") : "") + " " + i18n.message("query.condition.widget.like") + " ";
			var labelValue = inputValue != null && inputValue.length > 0 ? inputValue : "-";
			$field.data("cbQl", label + "'" + inputValue.replace("'", "\\'") + "'");
			that.settings.widgetContainer.find("#textField_" + fieldId + " .labelValue").text(label + labelValue);
			fixMenuPosition();
		};

		var $textFieldMenu = $("<div>", { "class": "ui-multiselect-menu ui-widget ui-widget-content ui-corner-all queryConditionSelector textFieldMenu", "data-field-id" : fieldId });
		$textFieldMenu.click(function(e) {
			e.stopPropagation();
		});

		var $textMenuCont1 = $("<span>", { "class" : "textMenuCont"});
		var $fieldLabel = $("<span>", { "class": "fieldLabel"});
		$fieldLabel.text(i18n.message("query.condition.widget.text"));
		$textMenuCont1.append($fieldLabel);
		var $inputField = $("<input>", { "class": "inputValue", "type": "text", "value" : conditionText && conditionText.length > 0 ? conditionText : ""});
		$inputField.change(function() {
			textFieldChangeHandler($(this), fieldId);
		});
		$textMenuCont1.append($inputField);

		var $textMenuCont2 = $("<span>", { "class" : "textMenuCont"});
		var $notCheckBox = $("<input>", {"class": "notCheckBox", "type" : "checkbox", id: "notCheckBox_" + fieldId});
		if (isNot) {
			$notCheckBox.prop("checked", "checked");
		}
		$notCheckBox.change(function() {
			textFieldChangeHandler($(this), fieldId);
		});
		$textMenuCont2.append($notCheckBox);
		$textMenuCont2.append($("<label>", { for: "notCheckBox_" + fieldId}).text("not"));

		$textFieldMenu.append($textMenuCont1);
		$textFieldMenu.append($textMenuCont2);

		$textFieldMenu.append($("<div>", { "class": "textMenuContInfo"}).text(i18n.message("query.condition.widget.text.field.info")));

		$("body").append($textFieldMenu);

		$inputField.change();

		$textField.click(function(e) {
			e.stopPropagation();
			$(".ui-multiselect-menu").hide();
			var $menu = $('.textFieldMenu[data-field-id="'+ fieldId + '"]');
			$menu.toggle();
			fixMenuPosition();
		});

		$("html").click(function() {
			$(".queryConditionSelector.textFieldMenu").each(function() {
				if ($(this).is(":visible")) {
					$(this).hide();
				}
			});
		});
	};

	var getNumberFieldContainer = function(fieldId, fieldName, fieldLabel, type, number) {
		var that = this;

		that.menuItemsDisabled.push(parseInt(fieldId, 10));
		var typeMap = {
			">" : "greater than",
			">=" : "greater than or equals to",
			"=" : "equals",
			"<=" : "less than or equals to",
			"<" : "less than"
		};
		var $textField = $("<button>", { id: "numberField_" + fieldId, "type": "button", "class" : "ui-multiselect ui-widget ui-state-default ui-corner-all queryConditionSelector numberField" });
		$textField.data("cbQLAttrName", cbQLNumberAttrMap[fieldName]);
		$textField.append($("<span>", {"class" : "ui-icon ui-icon-triangle-1-s"}));

		var $deleteButton = $("<span>",{ "class" : "ui-icon removeIcon"});
		$deleteButton.data("fieldId", fieldId);
		$deleteButton.click(function(e) {
			e.preventDefault();
			var fieldId = parseInt($(this).data("fieldId"), 10);
			$(this).closest("button").remove();
			var index = $.inArray(fieldId, that.menuItemsDisabled);
			if (index > -1) {
				that.menuItemsDisabled.splice(index, 1);
			}
		});
		$textField.prepend($deleteButton);

		var $textFieldLabel = $("<span>").html("<b>" + fieldLabel + "</b>: ");
		$textField.append($textFieldLabel);
		$textField.append($("<span>", { class: "labelValue"}).text(" " + (typeMap[type] ? typeMap[type] : "-") + " " + number ? number : ""));
		that.settings.addButton.before($textField);

		var fixMenuPosition = function() {
			var $menu = $('.numberFieldMenu[data-field-id="'+ fieldId + '"]');
			$menu.position({
				of: that.settings.widgetContainer.find("#numberField_" + fieldId),
				my: "left top",
				at: "left bottom"
			});
		};

		var textFieldChangeHandler = function(element, fieldId) {
			var $menuCont = element.closest(".numberFieldMenu");
			var selectedOperator = $menuCont.find(".operatorType").val();
			var fieldLabel = $menuCont.find('.operatorType option[value="' + selectedOperator + '"]').text();
			var number = $menuCont.find(".numberInput").val();
			if (!number || (number && (number.length == "" || number < 0))) {
				number = 0;
			}
			var labelValue = fieldLabel + " " + number;
			$("#numberField_" + fieldId).data("operatorAndValue", selectedOperator + " " + number);
			that.settings.widgetContainer.find("#numberField_" + fieldId + " .labelValue").text(labelValue);
			fixMenuPosition();
		};

		var $textFieldMenu = $("<div>", { "class": "ui-multiselect-menu ui-widget ui-widget-content ui-corner-all queryConditionSelector numberFieldMenu", "data-field-id" : fieldId });
		$textFieldMenu.click(function(e) {
			e.stopPropagation();
		});

		var $textMenuCont1 = $("<span>", { "class" : "textMenuCont"});
		var $fieldLabel = $("<span>", { "class": "fieldLabel"});
		$fieldLabel.text("Number");
		$textMenuCont1.append($fieldLabel);
		var $typeSelector = $("<select>", { "class": "operatorType" });
		for (var key in typeMap) {
			var $option = $("<option>", { value: key}).text(typeMap[key]);
			if (type == key) {
				$option.prop("selected", "selected");
			}
			$typeSelector.append($option);
		}
		$typeSelector.change(function() {
			textFieldChangeHandler($(this), fieldId);
		});
		$textMenuCont1.append($typeSelector);
		var $inputField = $("<input>", { class: "numberInput", "type": "number", "value" : number});
		$inputField.change(function() {
			textFieldChangeHandler($(this), fieldId);
		});
		$textMenuCont1.append($inputField);

		$textFieldMenu.append($textMenuCont1);

		$("body").append($textFieldMenu);

		$inputField.change();

		$textField.click(function(e) {
			e.stopPropagation();
			$(".ui-multiselect-menu").hide();
			var $menu = $('.numberFieldMenu[data-field-id="'+ fieldId + '"]');
			$menu.toggle();
			fixMenuPosition();
		});

		$("html").click(function() {
			$(".queryConditionSelector.numberFieldMenu").each(function() {
				if ($(this).is(":visible")) {
					$(this).hide();
				}
			});
		});
	};

	var getOrderByContainer = function(orderById, value, desc) {
		var that = this;

		var $textField = $("<button>", { id: "orderByField_" + orderById, "type": "button", "class" : "ui-multiselect ui-widget ui-state-default ui-corner-all queryConditionSelector orderByField" });
		$textField.append($("<span>", {"class" : "ui-icon ui-icon-triangle-1-s"}));

		var $deleteButton = $("<span>",{ "class" : "ui-icon removeIcon"});
		$deleteButton.click(function(e) {
			e.preventDefault();
			$(this).closest("button").remove();
		});
		$textField.prepend($deleteButton);

		var $textFieldLabel = $("<span>").html("<b>" + i18n.message("tracker.view.orderBy.label") + "</b>: ");
		$textField.append($textFieldLabel);
		$textField.append($("<span>", { class: "labelValue"}).text((orderBy[value] ? orderBy[value] : "-") + " " + (desc ? " DESC" : "")));
		that.settings.addButton.before($textField);

		var fixMenuPosition = function() {
			var $menu = $('.orderByFieldMenu[data-field-id="'+ orderById + '"]');
			$menu.position({
				of: that.settings.widgetContainer.find("#orderByField_" + orderById),
				my: "left top",
				at: "left bottom"
			});
		};

		var textFieldChangeHandler = function(element, fieldId) {
			var $menuCont = element.closest(".orderByFieldMenu");
			var isDesc = $menuCont.find(".descCheckBox").is(":checked");
			var selectedField = $menuCont.find(".fieldType").val();
			var fieldLabel = $menuCont.find('.fieldType option[value="' + selectedField + '"]').text();
			var $field = that.settings.widgetContainer.find("#orderByField_" + fieldId);
			$field.data("fieldName", selectedField == "-" ? "" : selectedField);
			$field.data("isDesc", isDesc);
			var labelValue = fieldLabel + (isDesc ? " DESC" : "");
			that.settings.widgetContainer.find("#orderByField_" + fieldId + " .labelValue").text(labelValue);
			fixMenuPosition();
		};

		var $textFieldMenu = $("<div>", { "class": "ui-multiselect-menu ui-widget ui-widget-content ui-corner-all queryConditionSelector orderByFieldMenu", "data-field-id" : orderById });
		$textFieldMenu.click(function(e) {
			e.stopPropagation();
		});

		var $textMenuCont1 = $("<span>", { "class" : "textMenuCont"});
		var $fieldLabel = $("<span>", { "class": "fieldLabel"});
		$fieldLabel.text(i18n.message("tracker.view.orderBy.label"));
		$textMenuCont1.append($fieldLabel);
		var $typeSelector = $("<select>", { class: "fieldType"});
		$typeSelector.append($("<option>", { value: "-"}).text("-"));
		for (var key in orderBy) {
			var $option = $("<option>", { value: key}).text(orderBy[key]);
			if (value == key) {
				$option.prop("selected", "selected");
			}
			$typeSelector.append($option);
		}
		$typeSelector.change(function() {
			textFieldChangeHandler($(this), orderById);
		});
		$textMenuCont1.append($typeSelector);

		var $descCheckBox = $("<input>", {"class": "descCheckBox", "type" : "checkbox", id: "descCheckBox_" + orderById});
		if (desc) {
			$descCheckBox.prop("checked", "checked");
		}
		$descCheckBox.change(function() {
			textFieldChangeHandler($(this), orderById);
		});
		$textMenuCont1.append($descCheckBox);
		$textMenuCont1.append($("<label>", { for: "descCheckBox_" + orderById}).text("DESC"));

		$textFieldMenu.append($textMenuCont1);

		$("body").append($textFieldMenu);

		$typeSelector.change();

		$textField.click(function(e) {
			e.stopPropagation();
			$(".ui-multiselect-menu").hide();
			var $menu = $('.orderByFieldMenu[data-field-id="'+ orderById + '"]');
			$menu.toggle();
			fixMenuPosition();
		});

		$("html").click(function() {
			$(".queryConditionSelector.orderByFieldMenu").each(function() {
				if ($(this).is(":visible")) {
					$(this).hide();
				}
			});
		});

		that.nextOrderById++;
	};

	var getDatePickers = function(fieldId, fieldName, fieldLabel, existingFromDate, existingToDate) {
		var that = this;

		that.menuItemsDisabled.push(parseInt(fieldId, 10));

		var decodeRange = function(rangeString) {
			return {
				direction: rangeString.charAt(0),
				range: rangeString.charAt(rangeString.length - 1),
				number: rangeString.replace(/[^0-9\.]/g, '')
			}
		};

		var fromDateRange = null;
		var toDateRange = null;
		if (existingFromDate && (startsWith(existingFromDate, "+") || startsWith(existingFromDate, "-"))) {
			fromDateRange = decodeRange(existingFromDate);
		}
		if (existingToDate && (startsWith(existingToDate, "+") || startsWith(existingToDate, "-"))) {
			toDateRange = decodeRange(existingToDate);
		}

		if (fromDateRange == null && existingFromDate != null && existingFromDate.length > 0) {
			existingFromDate = $.datepicker.formatDate(DateParsing.simpleDateFormatMapping[DateParsing.dateFormat], new Date(existingFromDate));
		} else {
			existingFromDate = "";
		}
		if (toDateRange == null && existingToDate != null && existingToDate.length > 0) {
			existingToDate =  $.datepicker.formatDate(DateParsing.simpleDateFormatMapping[DateParsing.dateFormat], new Date(existingToDate));
		} else {
			existingToDate = "";
		}

		var $dateField = $("<button>", { id: "dateField_" + fieldId, "type": "button", "class" : "ui-multiselect ui-widget ui-state-default ui-corner-all queryConditionSelector dateField" });
		$dateField.data("cbQLAttrName", cbQLDateAttrMap[fieldName]);
		$dateField.append($("<span>", {"class" : "ui-icon ui-icon-triangle-1-s"}));

		var $deleteButton = $("<span>",{ "class" : "ui-icon removeIcon"});
		$deleteButton.data("fieldId", fieldId);
		$deleteButton.click(function(e) {
			e.preventDefault();
			var fieldId = parseInt($(this).data("fieldId"), 10);
			$(this).closest("button").remove();
			var index = $.inArray(fieldId, that.menuItemsDisabled);
			if (index > -1) {
				that.menuItemsDisabled.splice(index, 1);
			}
		});
		$dateField.prepend($deleteButton);

		var $dateFieldLabel = $("<span>").html("<b>" + fieldLabel + "</b>:");
		$dateField.append($dateFieldLabel);
		$dateField.append($("<span>",  {"class" : "fromDate"}).text(existingFromDate ? " " + i18n.message("query.condition.widget.from") + " " + existingFromDate : ""));
		$dateField.append($("<span>",  {"class" : "toDate"}).text(existingToDate ? " " + i18n.message("query.condition.widget.to") + " " + existingToDate : ""));
		that.settings.addButton.before($dateField);

		var fixMenuPosition = function() {
			var $menu = $('.dateFieldMenu[data-field-id="'+ fieldId + '"]');
			$menu.position({
				of: that.settings.widgetContainer.find("#dateField_" + fieldId),
				my: "left top",
				at: "left bottom"
			});
		};

		var getISODate = function(date) {
			return $.datepicker.formatDate("yy-mm-dd", date);
		};

		var dateFieldChangeHandler = function(dateFieldId, otherDateFieldId, label, dataAttrName, value) {
			var $field = that.settings.widgetContainer.find("#dateField_" + fieldId);
			var isoDate = value != null && value.length > 0 ? getISODate(jQueryDatepickerHelper.getDate(dateFieldId, otherDateFieldId)) : "";
			var labelValue = value != null && value.length > 0 ? value : "-";
			$field.data(dataAttrName, isoDate);
			that.settings.widgetContainer.find("#dateField_" + fieldId + " ." + dataAttrName).text(" " + label + " " + labelValue);
			fixMenuPosition();
		};

		var getByTodayRangeString = function(type) {
			var number = $("#" + type + "Number_" + fieldId).val();
			if (number < 1 || number.length == 0) {
				number = 1;
			}
			var period = $("#" + type + "Period_" + fieldId).val();
			var beforeAfter = $("#" + type + "BeforeAfter_" + fieldId).val();
			return "" + beforeAfter + number + period + "";
		};

		var dateFieldByTodayChangeHandler = function($number) {
			if ($number) {
				var value = $number.val();
				if (value < 1 || value.length == 0) {
					$number.val(1);
				}
			}
			var $field = that.settings.widgetContainer.find("#dateField_" + fieldId);
			var fromDate = getByTodayRangeString("from");
			var toDate = getByTodayRangeString("to");
			$field.data("fromDate", fromDate);
			$field.data("toDate", toDate);
			that.settings.widgetContainer.find("#dateField_" + fieldId + " .fromDate").text(" from " + fromDate);
			that.settings.widgetContainer.find("#dateField_" + fieldId + " .toDate").text(" to " + toDate);
			fixMenuPosition();
		};

		var $dateFieldMenu = $("<div>", { "class": "ui-multiselect-menu ui-widget ui-widget-content ui-corner-all queryConditionSelector dateFieldMenu", "data-field-id" : fieldId });
		$dateFieldMenu.click(function(e) {
			e.stopPropagation();
		});
		var fromId = "datepicker_" + fieldId + "_from";
		var toId = "datepicker_" + fieldId + "_to";

		var $byDateDiv = $("<div>", { "class": "dateTypeLabel"});
		var $byDateInput = $("<input>",{ id: "byDate_" + fieldId, type: "radio", name: "dateFieldType", value: "byDate"});
		$byDateInput.change(function() {
			if ($(this).is(":checked")) {
				var fromValue = $('.dateFieldMenu[data-field-id="' + fieldId + '"]').find("input.dateInput.fromDate").val();
				dateFieldChangeHandler(fromId, toId, i18n.message("query.condition.widget.from"), "fromDate", fromValue);
				var toValue = $('.dateFieldMenu[data-field-id="' + fieldId + '"]').find("input.dateInput.toDate").val();
				dateFieldChangeHandler(toId, fromId, i18n.message("query.condition.widget.to"), "toDate", toValue);
			}
		});
		if (fromDateRange == null) {
			$byDateInput.prop("checked", "checked");
			$byDateInput.change();
		}
		$byDateDiv.append($byDateInput);
		$byDateDiv.append($("<label>", { for: "byDate_" + fieldId}).text(i18n.message("query.condition.widget.range.by.date")));
		$dateFieldMenu.append($byDateDiv);

		var $dateFromCont = $("<div>", { "class" : "dateMenuCont"});
		$dateFromCont.append($("<span>", { "class" : "fieldLabel"}).text(i18n.message("query.condition.widget.from")));
		var $dateFrom = $("<input>", { "class": "dateInput fromDate", id: fromId, type: "text"});
		$dateFrom.change(function() {
			dateFieldChangeHandler(fromId, toId, i18n.message("query.condition.widget.from"), "fromDate", $(this).val());
		});
		$dateFrom.focus(function() {
			$("#byDate_" + fieldId).prop("checked", "checked");
		});
		$dateFrom.val(existingFromDate);
		$dateFromCont.append($dateFrom);
		$dateFieldMenu.append($dateFromCont);

		var $dateToCont = $("<div>", { "class" : "dateMenuCont"});
		$dateToCont.append($("<span>", { "class" : "fieldLabel"}).text(i18n.message("query.condition.widget.to")));
		var $dateTo = $("<input>", { "class": "dateInput toDate", id: toId, type: "text"});
		$dateTo.change(function() {
			dateFieldChangeHandler(toId, fromId, i18n.message("query.condition.widget.to"), "toDate", $(this).val());
		});
		$dateTo.focus(function() {
			$("#byDate_" + fieldId).prop("checked", "checked");
		});
		$dateTo.val(existingToDate);
		$dateToCont.append($dateTo);
		$dateFieldMenu.append($dateToCont);


		var $byTodayDiv = $("<div>", { "class": "dateTypeLabel"});
		var $byTodayInput = $("<input>",{ id: "byToday_" + fieldId, type: "radio", name: "dateFieldType", value: "byToday"});
		$byTodayInput.change(function() {
			if ($(this).is(":checked")) {
				dateFieldByTodayChangeHandler();
			}
		});
		if (fromDateRange != null) {
			$byTodayInput.prop("checked", "checked");
		}
		$byTodayDiv.append($byTodayInput);
		$byTodayDiv.append($("<label>", { for: "byToday_" + fieldId}).text(i18n.message("query.condition.widget.range.by.period")));
		$dateFieldMenu.append($byTodayDiv);

		var $dateFromCont = $("<div>", { "class" : "dateMenuCont"});
		$dateFromCont.append($("<span>", { "class" : "fieldLabel"}).text(i18n.message("query.condition.widget.from")));
		var $fromNumber = $("<input>", { id: "fromNumber_" + fieldId, class: "numberInput", type: "number", size: "2", placeholder: "1"});
		if (fromDateRange != null) {
			$fromNumber.val(fromDateRange.number);
		}
		$fromNumber.focus(function() {
			$("#byToday_" + fieldId).prop("checked", "checked");
		});
		$fromNumber.change(function() {
			dateFieldByTodayChangeHandler($(this));
		});
		$dateFromCont.append($fromNumber);
		var $fromPeriod = $("<select>", { id: "fromPeriod_" + fieldId} );
		$fromPeriod.focus(function() {
			$("#byToday_" + fieldId).prop("checked", "checked");
		});
		$fromPeriod.change(function() {
			dateFieldByTodayChangeHandler();
		});
		$fromPeriod.append($("<option>", { value: "d"}).text(i18n.message("query.condition.widget.date.days")));
		$fromPeriod.append($("<option>", { value: "w"}).text(i18n.message("query.condition.widget.date.weeks")));
		$dateFromCont.append($fromPeriod);
		if (fromDateRange != null) {
			$fromPeriod.val(fromDateRange.range);
		}
		var $fromBeforeAfter = $("<select>", { id: "fromBeforeAfter_" + fieldId} );
		$fromBeforeAfter.focus(function() {
			$("#byToday_" + fieldId).prop("checked", "checked");
		});
		$fromBeforeAfter.change(function() {
			dateFieldByTodayChangeHandler();
		});
		$fromBeforeAfter.append($("<option>", { value: "-"}).text(i18n.message("query.condition.widget.date.before")));
		$fromBeforeAfter.append($("<option>", { value: "+"}).text(i18n.message("query.condition.widget.date.after")));
		$dateFromCont.append($fromBeforeAfter);
		if (fromDateRange != null) {
			$fromBeforeAfter.val(fromDateRange.direction);
		}
		$dateFromCont.append($("<span>").text(i18n.message("query.condition.widget.date.today")));
		$dateFieldMenu.append($dateFromCont);

		var $dateToCont = $("<div>", { "class" : "dateMenuCont"});
		$dateToCont.append($("<span>", { "class" : "fieldLabel"}).text(i18n.message("query.condition.widget.to")));
		var $toNumber = $("<input>", { id: "toNumber_" + fieldId, class: "numberInput", type: "number", size: "2", placeholder: "1"});
		if (toDateRange != null) {
			$toNumber.val(toDateRange.number);
		}
		$toNumber.focus(function() {
			$("#byToday_" + fieldId).prop("checked", "checked");
		});
		$toNumber.change(function() {
			dateFieldByTodayChangeHandler($(this));
		});
		$dateToCont.append($toNumber);
		var $toPeriod = $("<select>", { id: "toPeriod_" + fieldId} );
		$toPeriod.focus(function() {
			$("#byToday_" + fieldId).prop("checked", "checked");
		});
		$toPeriod.change(function() {
			dateFieldByTodayChangeHandler();
		});
		$toPeriod.append($("<option>", { value: "d"}).text(i18n.message("query.condition.widget.date.days")));
		$toPeriod.append($("<option>", { value: "w"}).text(i18n.message("query.condition.widget.date.weeks")));
		$dateToCont.append($toPeriod);
		if (toDateRange != null) {
			$toPeriod.val(toDateRange.range);
		}
		var $toBeforeAfter = $("<select>", { id: "toBeforeAfter_" + fieldId} );
		$toBeforeAfter.focus(function() {
			$("#byToday_" + fieldId).prop("checked", "checked");
		});
		$toBeforeAfter.change(function() {
			dateFieldByTodayChangeHandler();
		});
		$toBeforeAfter.append($("<option>", { value: "-"}).text(i18n.message("query.condition.widget.date.before")));
		$toBeforeAfter.append($("<option>", { value: "+"}).text(i18n.message("query.condition.widget.date.after")));
		$dateToCont.append($toBeforeAfter);
		if (toDateRange != null) {
			$toBeforeAfter.val(toDateRange.direction);
		}
		$dateToCont.append($("<span>").text(i18n.message("query.condition.widget.date.today")));
		$dateFieldMenu.append($dateToCont);

		$("body").append($dateFieldMenu);

		if (fromDateRange == null) {
			$byDateInput.change();
		} else {
			$byTodayInput.change();
		}

		fixMenuPosition();

		$dateFrom.click(function() {
			jQueryDatepickerHelper.initCalendar(fromId, toId, '', false);
		});
		$dateTo.click(function() {
			jQueryDatepickerHelper.initCalendar(toId, fromId, '', false);
		});

		$dateField.click(function(e) {
			e.stopPropagation();
			$(".ui-multiselect-menu").hide();
			var $menu = $('.dateFieldMenu[data-field-id="'+ fieldId + '"]');
			$menu.toggle();
			fixMenuPosition();
		});

		$("html").click(function() {
			$(".queryConditionSelector.dateFieldMenu").each(function() {
				if ($(this).is(":visible")) {
					$(this).hide();
				}
			});
		});

	};

	var getFields = function() {
		var that = this;

		var trackerIds = that.getTrackerIds();
		that.showAjaxLoading();
		$.getJSON(that.settings.getFieldsUrl, { tracker_id_list: trackerIds.join(",") }).done(function(result) {
			var items = {};
			for (var i=0; i < result.length; i++) {
				var field = result[i];
				var menuKey = field.id + "_" + field.typeId + "_" + field.name;
				items[menuKey] = {
					name: field.label,
					callback: function(itemKey, opt) {
						var fieldProps = itemKey.split("_");
						var fieldId = fieldProps[0];
						var fieldTypeId = fieldProps[1];
						var fieldName = fieldProps[2];
						if (fieldTypeId == 6) { // choiceField
							that.getFieldChoices(that.getProjectIds(), that.getTrackerIds(), fieldId, fieldName, items[itemKey].name, itemKey);
						}
						if (fieldTypeId == 3) { // date
							that.getDatePickers(fieldId, fieldName, items[itemKey].name, null, null, itemKey);
						}
						if (fieldTypeId == 0 || fieldTypeId == 10) { // text
							that.getTextFieldContainer(fieldId, fieldName, items[itemKey].name);
						}
						if (fieldTypeId == 5) { // users
							that.getUserChoices(fieldId, fieldName, items[itemKey].name);
						}
						if (fieldTypeId == 1) {
							that.getNumberFieldContainer(fieldId, fieldName, items[itemKey].name);
						}
					}
				};
			}
			items["referenceTo"] = {
				name: i18n.message("query.condition.widget.reference.to"),
				callback: function(itemKey, opt) {
					that.getReferenceSelector(i18n.message("query.condition.widget.reference.to"), "referenceToId");
				}
			};
			items["referenceFrom"] = {
				name: i18n.message("query.condition.widget.reference.from"),
				callback: function(itemKey, opt) {
					that.getReferenceSelector(i18n.message("query.condition.widget.reference.from"), "referenceFromId");
				}
			};
			items["separator"] = "---";
			items["orderBy"] = {
				name: i18n.message("tracker.view.orderBy.label"),
				callback: function() {
					that.getOrderByContainer(that.nextOrderById, "-", false);
				}
			};
			that.menuItems = items;
			that.hideAjaxLoading();
		});
	};

	var initAddButton = function() {
		var that = this;

		var $addButton = that.settings.widgetContainer.find(".addButton");
		if ($("body").hasClass("FF")) {
			$addButton.css("position", "relative");
			$addButton.css("top", "-1px");
		}

		var menu = new ContextMenuManager( {
			selector: "#" + that.settings.widgetContainer.attr("id") + " .addButton",
			trigger: "left",
			build: function() {
				for (var key in that.menuItems) {
					var keyAttrs = key.split("_");
					var fieldId = parseInt(keyAttrs[0], 10);
					that.menuItems[key].disabled = $.inArray(fieldId, that.menuItemsDisabled) > -1;
				}
				that.menuItems["referenceTo"].disabled = $.inArray("referenceToId", that.menuItemsDisabled) > -1;
				that.menuItems["referenceFrom"].disabled = $.inArray("referenceFromId", that.menuItemsDisabled) > -1;
				return {
					items: that.menuItems
				};
			}
		});
		menu.render();
	};

	var initContainer = function(queryId, queryString) {
		var that = this;

		var populateInitDataModel = function(result) {

			var projectIds = [];
			var trackerIds = [];
			var choiceFields = {};
			var dateFields = {};
			var textFields = {};
			var userFields = {};
			var referenceFields = {};
			var numberFields = {};

			var orderByModel = [];
			var fieldOrder = [];

			var populateModel = function(node) {
				if (node.hasOwnProperty("left")) {
					if (node.left.hasOwnProperty("value")) {
						if (node.left.value == "project.id") {
							projectIds.push(node.right.value);
							fieldOrder.push("project");
						} else if (node.left.value == "tracker.id") {
							trackerIds.push(node.right.value);
							fieldOrder.push("tracker");
						} else {

							var fieldName = node.left.value;
							var fieldAttr = fieldName;

							if ($.inArray(fieldName, cbQLMeaningAttrs) > -1) {

								var fieldValues = choiceFields.hasOwnProperty(fieldName) ? choiceFields[fieldName] : [];
								var value = node.right.value;
								// Remove apostrophes
								if (value.lastIndexOf("\'", 0) === 0) {
									value = value.substring(1, value.length - 1);
								}
								fieldValues.push(value);
								choiceFields[fieldName] = fieldValues;

							} else if ($.inArray(fieldName, cbQLTextAttrs) > -1) {

								var operator = node.operator;
								if (operator == "LIKE" || operator == "NOTLIKE") {
									var fieldValues = textFields.hasOwnProperty(fieldName) ? textFields[fieldName] : {};
									var inputValue = node.right.value;
									// Remove apostrophes
									if (inputValue.lastIndexOf("\'", 0) === 0) {
										inputValue = inputValue.substring(1, inputValue.length - 1);
									}
									fieldValues["isNot"] = operator == "NOTLIKE";
									fieldValues["inputValue"] = inputValue;
									textFields[fieldName] = fieldValues;
								}

							} else if ($.inArray(fieldName, cbQLReferenceAttrs) > -1) {

								var fieldValues = referenceFields.hasOwnProperty(fieldName) ? referenceFields[fieldName] : [];
								var value = node.right.value;
								fieldValues.push(value);
								referenceFields[fieldName] = fieldValues;

							} else if ($.inArray(fieldName, cbQLNumberAttrs) > -1) {

								var values = numberFields.hasOwnProperty(fieldName) ? numberFields[fieldName] : {};
								values["numberValue"] = node.right.value;
								values["operator"] = node.operator;
								numberFields[fieldName] = values;

							} else {

								var isDateAttr = false;
								for (var key in cbQLDateAttrMap) {
									var dateAttr = cbQLDateAttrMap[key];
									if (dateAttr == fieldName) {
										isDateAttr = true;
										break;
									}
								}

								var isUserAttr = false;
								for (var key in cbQLUserAttrMap) {
									var userAttr = cbQLUserAttrMap[key];
									if (userAttr == fieldName || (userAttr + "Role") == fieldName) {
										isUserAttr = true;
										break;
									}
								}

								if (isDateAttr) {
									var values = dateFields.hasOwnProperty(fieldName) ? dateFields[fieldName] : {};
									var value = node.right.value;
									// Remove apostrophes if present
									if (value.lastIndexOf("\'", 0) === 0) {
										value = value.substring(1, value.length - 1);
									}
									if (node.operator == ">=") {
										values["from"] = value;
									}
									if (node.operator == "<=") {
										values["to"] = value;
									}
									dateFields[fieldName] = values;
								} else if (isUserAttr) {
									var values = userFields.hasOwnProperty(fieldName) ? userFields[fieldName] : [];
									var userValue = node.right.value;
									if (userValue == "'current user'") {
										userValue = -1;
									}
									values.push(userValue);
									userFields[fieldName] = values;
								} else {
									if (fieldName.lastIndexOf("\'", 0) === 0) {
										fieldName = fieldName.substring(1, fieldName.length-1);
									}
									var fieldNameProps = fieldName.split(".");
									var projectId = fieldNameProps[0];
									var trackerId = fieldNameProps[1];
									fieldAttr = fieldNameProps[2];
									var fieldValues = choiceFields.hasOwnProperty(fieldAttr) ? choiceFields[fieldAttr] : [];
									var value = node.right.value;
									// Remove apostrophes
									if (value.lastIndexOf("\'", 0) === 0) {
										value = value.substring(1, value.length-1);
									}
									fieldValues.push(projectId + "_" + trackerId + "_" + value);
									choiceFields[fieldAttr] = fieldValues;
								}

							}
							if (fieldAttr == "workItemStatus") {
								fieldAttr = "Status";
							}
							if (fieldAttr == "workItemResolution") {
								fieldAttr = "Resolution";
							}
							if (fieldAttr == "assignedToRole") {
								fieldAttr = "assignedTo";
							}
							if (fieldAttr == "ownerRole") {
								fieldAttr = "owner";
							}
							if (fieldOrder[fieldOrder.length - 1] != fieldAttr) {
								fieldOrder.push(fieldAttr);
							}
						}
					} else {
						populateModel(node.left);
					}
				}
				if (node.hasOwnProperty("right")) {
					populateModel(node.right);
				}
			};

			populateModel(result.where);

			if (result.hasOwnProperty("orderBy") && result.orderBy.hasOwnProperty("value")) {
				for (var i=0; i < result.orderBy.value.length; i++) {
					var orderBy = result.orderBy.value[i];
					orderByModel.push(orderBy);
				}
			}

			that.initData = {
				"projectIds" : projectIds,
				"trackerIds" : trackerIds,
				"choiceFields" : choiceFields,
				"dateFields" : dateFields,
				"textFields" : textFields,
				"userFields" : userFields,
				"referenceFields" : referenceFields,
				"numberFields" : numberFields,
				"fieldOrder" : fieldOrder,
				"orderBy" : orderByModel
			};

		};

		if ((queryId == null || queryId === 0) && queryString == 'null') {
			that.getProjects(function() {
				that.getTrackers(function() {
					that.getFieldChoices(that.getProjectIds(), that.getTrackerIds(), 7, "Status", "Status");
				});
			});
		} else {
			var data = {};
			if (queryString != 'null') {
				data.query_string = queryString;
			} else if (queryId != null) {
				data.query_id = queryId;
			}
			$.getJSON(that.settings.getQueryStructureUrl, data,function(result) {
				populateInitDataModel(result);
				that.getProjects(function() {
					that.getTrackers(function() {

						for (var i = 0; i < that.initData.fieldOrder.length; i++) {
							var fieldAttr = that.initData.fieldOrder[i];
							if ($.inArray(fieldAttr, cbQLDateAttrs) > -1) {
								var values = that.initData.dateFields[fieldAttr];
								for (var key in cbQLDateAttrMap) {
									if (cbQLDateAttrMap[key] == fieldAttr) {
										fieldAttr = key;
										break;
									}
								}
								that.getDatePickers(fieldIdMap[fieldAttr], fieldAttr, fieldAttr, values.from, values.to);
							} else if ($.inArray(fieldAttr, cbQLTextAttrs)> -1) {
								var values = that.initData.textFields[fieldAttr];
								that.getTextFieldContainer(fieldIdMap[fieldAttr], fieldAttr, fieldAttr, values.isNot, values.inputValue);
							} else if ($.inArray(fieldAttr, cbQLUserAttrs) > -1) {
								for (var key in cbQLUserAttrMap) {
									if (cbQLUserAttrMap[key] == fieldAttr) {
										fieldAttr = key;
										break;
									}
								}
								that.getUserChoices(fieldIdMap[fieldAttr], fieldAttr, fieldAttr);
							} else if ($.inArray(fieldAttr, cbQLReferenceAttrs) > -1) {
								var label = "";
								var values = that.initData.referenceFields[fieldAttr];
								if (fieldAttr == "referenceToId") {
									label = i18n.message("query.condition.widget.reference.to");
								}
								if (fieldAttr == "referenceFromId") {
									label = i18n.message("query.condition.widget.reference.from");
								}
								that.getReferenceSelector(label, fieldAttr, values);
							} else if ($.inArray(fieldAttr, cbQLNumberAttrs) > -1) {
								var label = "";
								var values = that.initData.numberFields[fieldAttr];
								if (fieldAttr == "storyPoints") {
									label = "Story Points";
								}
								that.getNumberFieldContainer(fieldIdMap[label], label, label, values.operator, values.numberValue);
							} else if (fieldAttr != "project" && fieldAttr != "tracker") {
								that.getFieldChoices(that.getProjectIds(), that.getTrackerIds(), fieldIdMap[fieldAttr], fieldAttr, fieldAttr);
							}
						}

						for (var i = 0; i < that.initData.orderBy.length; i++) {
							var orderBy = that.initData.orderBy[i];
							that.getOrderByContainer(that.nextOrderById, orderBy.field, orderBy.direction == "DESC");
						}

					});
				});
			});
		}
	};

	var buildQueryString = function() {
		var that = this;

		var $widgetContainer = that.settings.widgetContainer;

		codebeamer.Queries.buildQueryString(that.settings.widgetContainer);
		$widgetContainer.find(".conditions .inputSection input").val($widgetContainer.data("queryString"));
	};

	var initSearchButton = function(queryId) {
		var that = this;

		that.settings.widgetContainer.find(".searchButton").click(function() {
			that.buildQueryString();
			var queryString = that.advancedMode ? that.settings.widgetContainer.find(".conditions .inputSection input").val() :
				encodeURIComponent(that.settings.widgetContainer.data("queryString"));
			if (queryString.length > 0) {
				ajaxBusyIndicator.showBusyPage();
				window.location.href = contextPath + "/proj/queries.spr?cbQl=" + queryString + ((queryId != null && queryId > 0) ? ("&queryId=" + queryId) : "") + (that.advancedMode ? "&advanced=true" : "");
			} else {
				codebeamer.Queries.showEmptyQueryAlert();
			}
		});
	};

	var initAdvanced = function(advanced, queryString) {
		var that = this;

		var advancedLink = that.settings.widgetContainer.parent().next(".advancedLink").find("a");
		advancedLink.click(function() {
			that.buildQueryString();
			var inputSection = that.settings.widgetContainer.find(".conditions .inputSection");
			var selectorSection = that.settings.widgetContainer.find(".conditions .selectorSection");
			if (!that.advancedMode) {
				$(this).text(i18n.message("queries.simple.label"));
				inputSection.find("input").val(that.settings.widgetContainer.data("queryString"));
				inputSection.show();
				selectorSection.hide();
			} else {
				$(this).text(i18n.message("queries.advanced.label"));
				selectorSection.show();
				inputSection.hide();
			}
			that.advancedMode = !that.advancedMode;
		});
		if (advanced) {
			advancedLink.click();
			that.settings.widgetContainer.find(".conditions .inputSection input").val(queryString);
		}
	};

	var showAjaxLoading = function() {
		var that = this;

		//console.log(that);
		that.ajaxLoading.show();
	};

	var hideAjaxLoading = function() {
		var that = this;

		that.ajaxLoading.hide();
	};

	var init = function(queryId, queryString, advanced, $widgetContainer, $resultContainer, config) {
		var that = this;

		//console.log(that);

		that.settings = {};
		that.ajaxLoading;
		that.initData = {};
		that.advancedMode = false;
		that.nextOrderById = 0;

		that.menuItemsDisabled = [];
		that.menuItems = {};

		that.ajaxLoading = $widgetContainer.find("#ajaxLoadingImg");
		
		that.settings = $.extend({}, {
			"widgetContainer" : $widgetContainer,
			"resultContainer" : $resultContainer,
			"addButton"		  : $widgetContainer.find(".addButton")
		}, config);

		that.initContainer(queryId, queryString);
		that.initAddButton();
		that.initAdvanced(advanced, queryString);
		that.initSearchButton(queryId);
		that.getFields();
	};

	return {
		"init": init,
		"reloadIfProjectChange": reloadIfProjectChange,
		"reloadIfTrackerChange": reloadIfTrackerChange,
		"destroyMultiSelect": destroyMultiSelect,
		"initMultiSelect": initMultiSelect,
		"checkSelectedValues": checkSelectedValues,
		"checkFirstOption": checkFirstOption,
		"getProjects": getProjects,
		"renderTrackers": renderTrackers,
		"getTrackersJSON": getTrackersJSON,
		"getTrackers": getTrackers,
		"renderFieldChoices": renderFieldChoices,
		"renderUsers": renderUsers,
		"getFieldChoicesJSON": getFieldChoicesJSON,
		"getUserChoicesJSON": getUserChoicesJSON,
		"getFieldChoices": getFieldChoices,
		"getUserChoices": getUserChoices,
		"getProjectIds": getProjectIds,
		"getTrackerIds": getTrackerIds,
		"decodeReferences": decodeReferences,
		"decodeReferenceValue": decodeReferenceValue,
		"getReferenceSelector": getReferenceSelector,
		"getTextFieldContainer": getTextFieldContainer,
		"getNumberFieldContainer": getNumberFieldContainer,
		"getOrderByContainer": getOrderByContainer,
		"getDatePickers": getDatePickers,
		"getFields": getFields,
		"initAddButton": initAddButton,
		"initContainer": initContainer,
		"buildQueryString": buildQueryString,
		"initSearchButton": initSearchButton,
		"initAdvanced": initAdvanced,
		"showAjaxLoading": showAjaxLoading,
		"hideAjaxLoading": hideAjaxLoading
	};

};